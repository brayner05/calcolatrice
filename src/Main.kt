import parsing.Parser
import reporting.ErrorReporter
import tokenization.Lexer
import kotlin.system.exitProcess


/**
 * A collection of commands that can be run in the REPL.
 */
internal val replCommands: Array<ReplCommand> = arrayOf(
    ReplCommand(
        name = "help",
        description = "Shows the help menu.",
        run = ::showHelp
    ),
    ReplCommand(
        name = "quit",
        description = "Exits the application.",
        run = {
            exitProcess(0)
        }
    ),
    ReplCommand(
        name = "clear",
        description = "Clears the screen if supported.",
        run = {
            print("\u001b[H\u001b[2J")
            System.out.flush()
        }
    )
)

/**
 * Print the definition of an expression and the name and description of
 * each supported command.
 */
internal fun showHelp() {
    println("Calcolatrice - Command Line Calculator")
    println("======================================\n")
    println("- Any line starting with '.' is interpreted as a command:")

    for (command in replCommands) {
        println(".${command.name}\t->\t${command.description}")
    }

    println()
    println("- An expression takes the form: `Expr -> Term ('+'|'-') Expression`.")
    println("Example: 12.34 * (5.6 + 7.89)")
}


/**
 * Run a defined REPL command. REPL commands are prefixed with a '.'.
 *
 * @param commandName The name of the command to execute.
 */
internal fun runReplCommand(commandName: String) {
    val command = replCommands.find { it.name == commandName }
    if (command == null) {
        ErrorReporter.report("Unknown command: $commandName")
        return
    }
    command.run()
}


/**
 * Compute the result of a raw expression, and print it to STDOUT.
 *
 * @param expression The raw expression to compute.
 */
internal fun computeExpression(expression: String) {
    try {
        // Tokenize raw input into a token stream.
        val tokens = Lexer(expression).scanAllTokens()

        // Parse the token stream into a parse tree.
        val parseTree = Parser(tokens).parse()

        // Evaluate the parse tree.
        val result = parseTree.evaluate()

        // Print the result of the parse tree.
        println("\t=\t${result.value}")
    } catch (ex: Error) {
        if (ex.message != null) {
            ErrorReporter.report(ex.message!!)
        }
    }
}


/**
 * Run the application REPL.
 */
internal fun runRepl() {
    var line: String
    while (true) {
        print("[calcolatrice] > ")
        line = readln()

        // Lines starting with a '.' are interpreted as commands.
        if (line.startsWith('.')) {
            val commandName = line.substring(1)
            runReplCommand(commandName)
            println()
            continue
        }

        // Compute and print the result of the expression.
        computeExpression(line)
        println()
    }
}

fun main(args: Array<String>) {
    println("Calcolatrice - CLI Calculator")
    runRepl()
}
