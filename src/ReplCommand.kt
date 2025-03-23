
typealias CommandAction = () -> Unit

data class ReplCommand(
    val name: String,
    val description: String,
    val run: CommandAction
)
