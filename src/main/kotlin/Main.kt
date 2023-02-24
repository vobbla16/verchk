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
import kotlinx.coroutines.flow.*
import utils.consoleTable
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

            println("Possibly installed on endpoint:")
            println(table.build())
        }
    }


    val showTargets = ShowTargets()
    val determineTargetSystem = DetermineTargetSystem()
    parser.subcommands(showTargets, determineTargetSystem)
    parser.parse(args)
}

suspend fun determineTargetSystem(url: String): List<TargetSystem> = coroutineScope {
    val impls = mutableListOf<TargetSystem>()

    val asyncedImpls = mutableListOf<Deferred<Pair<TargetSystem, Boolean>>>()

    getAllTargets().forEach { impl ->
        val ctor = impl.primaryConstructor!!
        val ins = ctor.call(url) as TargetSystem

        asyncedImpls.add( async {
            Pair(ins, ins.check())
        } )
    }

//    TODO("Fancy loading indicator (consoleSpinner)")

    asyncedImpls.forEach { defPair ->
        defPair.await().let {
            if (it.second) impls.add(it.first)
        }
    }

    return@coroutineScope impls.toList()
}