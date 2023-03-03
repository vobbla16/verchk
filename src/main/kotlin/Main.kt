import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import utils.*
import kotlin.reflect.full.*

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser("verchk")

    class ShowTargets : Subcommand("show-targets", "Show all available target systems") {
        override fun execute() {
            val table = consoleTable {
                headers("#", "Name", "CPE id")

                getAllTargets().forEachIndexed { i, impl ->
                    val ctor = impl.primaryConstructor!!
                    val ins = ctor.call("test") as TargetSystem

                    addRow(i.toString(), ins.targetName, ins.cpe)
                }
            }
            println("All available systems to scan:")
            println(table.build())
        }
    }

    class DetermineTargetSystem : Subcommand("determine", "Determine target system") {
        val url by argument(ArgType.String, "endpoint", "Url to find out system")
        override fun execute() {
            val possibleSystems = runBlocking { determineTargetSystem(url) }

            val table = consoleTable {
                headers("#", "Target system", "CPE id")
                possibleSystems.forEachIndexed { i, s ->
                    addRow(i.toString(), s.targetName, s.cpe)
                }
            }

            println()
            println("Possibly installed on endpoint:")
            println(table.build())
        }
    }

    class DetermineSystemVersion : Subcommand("determine-version", "Determine target system's version") {
        val url by argument(ArgType.String, "endpoint", "Url to find version of system installed on it")
        override fun execute() {
            val possibleSystems = runBlocking { determineTargetSystem(url) }
            println()

            val versions = runBlocking { determineVersion(possibleSystems) }

            val table = consoleTable {
                headers("#", "Target system", "Version")
                versions.forEachIndexed { i, vers ->
                    addRow(i.toString(), possibleSystems[i].targetName, vers)
                }
            }

            println("Possible versions of systems possibly installed on endpoint:")
            println(table.build())
        }
    }



    val showTargets = ShowTargets()
    val determineTargetSystem = DetermineTargetSystem()
    val determineSystemVersion = DetermineSystemVersion()
    parser.subcommands(showTargets, determineTargetSystem, determineSystemVersion)
    parser.parse(args)
}

suspend fun determineTargetSystem(url: String) = coroutineScope {
    val impls = mutableListOf<TargetSystem>()

    val initialLines = mutableListOf<Line>()
    val eventsChannel = Channel<ChangeAction>()

    val allChecksJobs = mutableListOf<Job>()

    getAllTargets().forEachIndexed { i, target ->
        val ins = target.primaryConstructor!!.call(url) as TargetSystem

        initialLines.add(Line(Status.Spinner, "Checking ${ins.targetName}"))
        
        allChecksJobs.add(launch {
            val res = ins.check()
            if (res) {
                eventsChannel.send(ChangeAction.ChangeStatus(i, Status.Success()))
                eventsChannel.send(ChangeAction.ChangeText(i, "Target is ${ins.targetName}"))
                impls.add(ins)
            } else {
                eventsChannel.send(ChangeAction.ChangeStatus(i, Status.Error()))
                eventsChannel.send(ChangeAction.ChangeText(i, "Target is not ${ins.targetName}"))
            }
        })
    }

    val mls = MultipleLineStatus(initialLines, eventsChannel)
    launch { mls.start() }

    allChecksJobs.forEach { it.join() }
    mls.stop()

    return@coroutineScope impls
}

suspend fun determineVersion(possibleSystems: List<TargetSystem>): List<String> = coroutineScope {
    val versions = mutableListOf<String>()

    val initialLines = mutableListOf<Line>()
    val eventsChannel = Channel<ChangeAction>()

    val checkJobs = mutableListOf<Job>()

    possibleSystems.forEachIndexed { i, sys ->
        initialLines.add(Line(Status.Spinner, "Trying to find out version of ${sys.targetName}"))

        checkJobs.add(launch {
            val res = sys.version()
            versions.add(res)
            eventsChannel.send(ChangeAction.ChangeStatus(i, Status.Success()))
            eventsChannel.send(ChangeAction.ChangeText(i, "Found ${sys.targetName} with version ${res}"))
        })
    }

    val mls = MultipleLineStatus(initialLines, eventsChannel)
    launch { mls.start() }
    
    checkJobs.forEach { it.join() }
    mls.stop()

    return@coroutineScope versions
}