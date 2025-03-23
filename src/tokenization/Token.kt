package tokenization

data class Token(var type: TokenType, var lexeme: String, var value: Any?) {
    val isOperator: Boolean
        get() = type in operators

    companion object {
        val operators = arrayOf(
            TokenType.Plus,
            TokenType.Minus,
            TokenType.Asterisk,
            TokenType.Slash
        )
    }
}

typealias TokenStream = List<Token>