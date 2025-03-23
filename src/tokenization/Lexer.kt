package tokenization

import reporting.ErrorReporter

/**
 * A class used to parse a token stream from
 * a source code string containing mathematical
 * expressions.
 */
class Lexer (private val _source: String) {
    private var _position: Int = 0
    private var _tokenStart: Int = 0
    private var _tokens: MutableList<Token> = mutableListOf()

    // Append a simple token to the token list
    // by its type.
    private fun appendToken(type: TokenType) {
        val token = Token(type, _source.substring(_tokenStart, _position), null)
        _tokens.add(token)
    }

    private fun hasNextChar(): Boolean {
        return _position < _source.length;
    }

    // Get the next character in the stream and
    // increase _position.
    private fun advance(): Char {
        return _source[_position++]
    }

    // Get the next character in the stream.
    private fun peek(): Char {
        if (!hasNextChar()) {
            return '\u0000'
        }
        return _source[_position]
    }

    private fun match(ch: Char): Boolean {
        return peek() == ch
    }

    // Continue scanning until the next character
    // is not a digit.
    private fun scanDigits() {
        while (hasNextChar() && peek().isDigit()) {
            advance()
        }
    }

    // Scan a number and add it to the token list.
    private fun appendNumberToken() {
        scanDigits()
        if (peek() == '.') {
            advance()
            scanDigits()
        }
        val lexeme = _source.substring(_tokenStart, _position)
        val value = lexeme.toDouble()
        _tokens.add(Token(TokenType.Number, lexeme, value))
    }

    // Scan the next token and add it to the token list
    private fun scanNext() {
        when (val ch = advance()) {
            ' ', '\r' -> {}
            '+' -> appendToken(TokenType.Plus)
            '-' -> appendToken(TokenType.Minus)
            '*' -> appendToken(TokenType.Asterisk)
            '/' -> appendToken(TokenType.Slash)
            '^' -> appendToken(TokenType.Caret)
            '(' -> appendToken(TokenType.LeftParenthesis)
            ')' -> appendToken(TokenType.RightParenthesis)
            else -> {
                if (ch.isDigit()) {
                    appendNumberToken()
                    return
                }
                ErrorReporter.report("Invalid character: $ch")
            }
        }
    }

    /**
     * Convert the source string to a stream of tokens.
     * @return the list of tokens parsed from the input string.
     */
    fun scanAllTokens(): TokenStream {
        while (hasNextChar()) {
            scanNext()
            _tokenStart = _position
        }
        _tokens.add(Token(TokenType.EndOfFile, "EOF", null))
        return _tokens.toList()
    }
}