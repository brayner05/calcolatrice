package tokenization

import reporting.ErrorReporter

/**
 * A class used to parse a token stream from
 * a source code string containing mathematical
 * expressions.
 */
@Suppress("SameParameterValue")
class Lexer (private val _source: String) {
    private var _position: Int = 0
    private var _tokenStart: Int = 0
    private var _tokens: MutableList<Token> = mutableListOf()

    companion object {
        val keywords: Map<String, Token> = mapOf(
            Pair("true", Token(TokenType.Boolean, "true", true)),
            Pair("false", Token(TokenType.Boolean, "false", false))
        )
    }

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

    private fun matchNext(ch: Char): Boolean {
        if (_position + 1 >= _source.length) {
            return false;
        }
        return _source[_position + 1] == ch
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

    private fun appendKeywordToken() {
        while (hasNextChar() && peek().isLetter()) {
            advance()
        }

        val keywordName = _source.substring(_tokenStart, _position)
        if (keywordName !in keywords) {
            throw Error("Invalid keyword: $keywordName")
        }

        val token = keywords[keywordName]!!
        _tokens.add(token)
    }

    private fun appendTokenIfMatch(ch: Char, onMatch: TokenType, otherwise: TokenType?) {
        if (match(ch)) {
            advance()
            appendToken(onMatch)
            return
        }

        if (otherwise == null) {
            throw Error("Invalid token sequence: $ch${peek()}")
        }

        appendToken(otherwise)
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
            '!' -> appendTokenIfMatch('=', TokenType.BangEqual, TokenType.Factorial)
            '~' -> appendToken(TokenType.LogicalNegation)

            '&' -> appendTokenIfMatch('&', TokenType.Conjunction, null)
            '|' -> appendTokenIfMatch('|', TokenType.Disjunction, null)
            '>' -> appendTokenIfMatch('=', TokenType.GreaterOrEqual, TokenType.GreaterThan)

            '<' -> {
                if (match('=') && matchNext('>')) {
                    advance()
                    advance()
                    appendToken(TokenType.BiCondition)
                } else if (match('=')) {
                    appendToken(TokenType.LessOrEqual)
                } else {
                    appendToken(TokenType.LessThan)
                }
            }

            '=' -> {
                if (match('=')) {
                    advance()
                    appendToken(TokenType.EqualEqual)
                } else if (match('>')) {
                    advance()
                    appendToken(TokenType.Implication)
                } else {
                    throw Error("Invalid token sequence: ${ch}${peek()}")
                }
            }

            else -> {
                if (ch.isDigit()) {
                    appendNumberToken()
                    return
                }

                if (ch.isLetter()) {
                    appendKeywordToken()
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