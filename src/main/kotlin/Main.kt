import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import utils.Resource
import utils.consoleSpinner
import utils.consoleTable
import utils.suspendToResourceWrapper
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
            println(table.build())

            runBlocking {
                lateinit var a: Job
                suspendToResourceWrapper {
                    val client = HttpClient(CIO)
                    val response: HttpResponse = client.get("https://api.myip.com")
                    return@suspendToResourceWrapper response.bodyAsText()
                }.onEach {
                    when (it) {
                        is Resource.Success -> {
                            a.cancelAndJoin()
                            println("Got data: ${it.data}")
                        }

                        is Resource.Loading -> {
                            a = launch { consoleSpinner("Loading site") }
                        }

                        is Resource.Error -> {
                            a.cancelAndJoin()
                            println("Error occurred: ${it.message}")
                        }
                    }
                }.collect()
            }
        }
    }

    val showTargets = ShowTargets()
    parser.subcommands(showTargets)
    parser.parse(args)
}
