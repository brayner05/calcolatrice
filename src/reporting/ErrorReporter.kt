package reporting

class ErrorReporter {
    companion object {
        fun report(error: Error) {
            print("[\u001B[1;31m")
            println(" error \u001B[0m] : ${error.fileName} line ${error.line} ~")
            println("\t ${error.message}")
        }

        fun report(fileName: String, line: UInt, message: String) =
            report(Error(fileName, line, message))

        fun report(message: String) = report("stdin", 0u, message)
    }
}