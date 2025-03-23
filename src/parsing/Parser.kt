package parsing

import reporting.Error
import tokenization.Token
import tokenization.TokenStream
import tokenization.TokenType

class Parser(private val tokenStream: TokenStream) {
    private var _position: Int = 0

    private val _hasNext: Boolean
        get() = _position < tokenStream.size

    private val _parseTree: ExpressionTree? = null

    private fun peek(): Token? {
        if (!_hasNext) {
            return null
        }
        return tokenStream[_position]
    }

    private fun advance(): Token {
        return tokenStream[_position++]
    }

    private fun parseExpression(): ParseTreeNode {
        var left = parseTerm()
        while (_hasNext && peek()!!.type == TokenType.Plus) {
            advance()
            left = BinaryOperatorNode(TokenType.Plus, left, parseTerm())
        }
        return left
    }

    private fun parseTerm(): ParseTreeNode {
        var left = parseFactor()

        if (left is TerminalNode && left.value.type != TokenType.Number) {
            throw Error("Expected a number")
        }

        while (_hasNext && peek()!!.type == TokenType.Asterisk) {
            advance()
            val right = parseFactor()

            if (right is TerminalNode && right.value.type != TokenType.Number) {
                throw Error("Expected a number")
            }

            left = BinaryOperatorNode(TokenType.Asterisk, left, right)
        }
        return left
    }

    private fun parseFactor(): ParseTreeNode {
        val nextToken = advance()
        var terminal: ParseTreeNode = TerminalNode(nextToken)

        if (nextToken.type == TokenType.LeftParenthesis) {
            terminal = parseExpression()
            if (!_hasNext || peek()!!.type != TokenType.RightParenthesis) {
                throw Error("Expected: )")
            }
            advance()
        }

        return terminal
    }


    fun parse(): ExpressionTree {
        val root = parseExpression()
        return ExpressionTree(root)
    }
}