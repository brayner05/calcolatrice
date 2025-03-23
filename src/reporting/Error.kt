package reporting

data class Error(
    val fileName: String,
    val line: UInt,
    val message: String
)
