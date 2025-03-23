import parsing.Parser
import reporting.Error
import reporting.ErrorReporter
import tokenization.Lexer
import java.io.File
import kotlin.system.exitProcess

typealias ReplCommand = () -> Unit

internal val replCommands: Map<String, ReplCommand> = mapOf(
    Pair("quit") {
        exitProcess(0)
    },
    Pair("clear") {
        print("\u001b[H\u001b[2J")
        System.out.flush()
    }
)


internal fun runReplCommand(commandName: String) {
    replCommands[commandName]?.invoke()
        ?: ErrorReporter.report("Unknown command: $commandName")
}


internal fun computeExpression(expression: String) {
    val tokens = Lexer(expression).scanAllTokens()
    val parseTree = Parser(tokens).parse()
    val result = parseTree.evaluate()
    println("\t=\t${result.value}")
}


internal fun runRepl() {
    var line: String = ""
    while (true) {
        println("Enter an expression.")
        print("[calcolatrice] > ")
        line = readln()

        if (line.startsWith('.')) {
            val commandName = line.substring(1)
            runReplCommand(commandName)
            continue
        }

        computeExpression(line)
    }
}

fun main(args: Array<String>) {
    println("Calcolatrice - CLI Calculator")
    runRepl()
}
