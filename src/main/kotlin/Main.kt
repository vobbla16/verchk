import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
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
            println(table.build())
        }
    }

    val showTargets = ShowTargets()
    parser.subcommands(showTargets)
    parser.parse(args)
}
