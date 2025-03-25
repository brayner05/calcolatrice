package parsing

import tokenization.Token
import tokenization.TokenStream
import tokenization.TokenType


/**
 * A class used to parse an expression tree (parse tree) from a
 * stream of tokens.
 *
 * @constructor Creates a new `Parser` for the expression `tokenStream`.
 * @param tokenStream A stream of tokens to parse.
 *
 * @see tokenization.Token For more information about tokens.
 * @see tokenization.TokenStream For more information about token streams.
 */
class Parser(private val tokenStream: TokenStream) {
    private var _position: Int = 0

    private val _hasNext: Boolean
        get() = (_position < tokenStream.size &&
                tokenStream[_position].type != TokenType.EndOfFile)

    private var _lastFactor: ParseTreeNode? = null

    companion object {
        private val _zero = TerminalNode(
            Token (
                type = TokenType.Number,
                lexeme = "0.0",
                value = 0.0
            )
        )

        private val _negativeOne = TerminalNode(
            Token(
                type = TokenType.Number,
                lexeme = "-1.0",
                value = -1.0
            )
        )
    }

    /**
     * Gets the next token in the token stream if applicable.
     *
     * @return The next token in the token stream, or `null` if no more
     * tokens exist.
     */
    private fun peek(): Token? {
        if (!_hasNext) {
            return null
        }
        return tokenStream[_position]
    }

    /**
     * Consumes and returns the next token in the token stream.
     * That is, gets the next token, and moves ahead by one token.
     *
     * @return The next token in the token stream.
     */
    private fun advance(): Token {
        return tokenStream[_position++]
    }

    /**
     * Whether a type of token is a valid term separator. That is, `type`
     * separates two terms. For example, `type` is a valid term separator
     * if and only if it can be used in the expression:
     * ```
     * Expr -> Term `type` Term
     * ```
     *
     * @return `true` if `type` can be used to separate two terms, otherwise `false`.
     */
    private fun isTermSeparator(type: TokenType) =
        (type == TokenType.Plus ||
            type == TokenType.Minus ||
            type == TokenType.Conjunction ||
            type ==TokenType.Disjunction)

    /**
     * Whether a type of token is a valid factor separator. That is, `type`
     * separates two factors. For example, `type` is a valid factor separator
     * if and only if it can be used in the expression:
     * ```
     * Term -> Factor `type` Factor
     * ```
     *
     * @return `true` if `type` can be used to separate two factors, otherwise `false`.
     */
    private fun isFactorSeparator(type: TokenType) =
        type == TokenType.Asterisk || type == TokenType.Slash

    /**
     * Parse an entire expression into the root node of a parse tree.
     *
     * @return The root node of the expression (parse) tree.
     */
    private fun parseExpression(): ParseTreeNode {
        if (!_hasNext) {
            return _zero
        }

        var left = parseTerm()

        while (_hasNext && isTermSeparator(peek()!!.type)) {
            val operator = advance()
            left = BinaryOperatorNode(operator.type, left, parseTerm())
        }

        return left
    }

    /**
     * Parse a term from an expression. A term is anything separated by
     * expression operators such as '+' and '-'.
     *
     * @return A `ParseTreeNode` representing a term.
     */
    private fun parseTerm(): ParseTreeNode {
        var left = parseFactor()

        if (left is TerminalNode && left.value.type !in TokenType.literals) {
            throw Error("Invalid left operand: ${left.value.value}")
        }

        while (_hasNext && isFactorSeparator(peek()!!.type)) {
            val operator = advance()
            val right = parseFactor()

            if (right is TerminalNode && right.value.type !in TokenType.literals) {
                throw Error("Invalid right operand: ${right.value.value}")
            }

            left = BinaryOperatorNode(operator.type, left, right)
        }
        return left
    }

    /**
     * Parse individual terminals or a nested expression if in parentheses.
     *
     * @return A terminal node or a nested expression.
     */
    private fun parseFactor(): ParseTreeNode {
        if (!_hasNext) {
            throw Error("Expected an expression.")
        }

        val currentToken = advance()
        _lastFactor = TerminalNode(currentToken)

        _lastFactor = when (currentToken.type) {
            TokenType.LeftParenthesis -> parseParentheses()
            TokenType.Minus -> parseNegative()
            TokenType.LogicalNegation -> parseLogicalNegation()
            TokenType.Number, TokenType.Boolean -> TerminalNode(currentToken)
            else -> throw Error("Unexpected token: ${currentToken.value}")
        }

        if (_hasNext && peek()!!.type == TokenType.Factorial) {
            _lastFactor = parseFactorial()
        }

        return _lastFactor!!
    }

    private fun parseFactorial(): UnaryOperatorNode {
        advance()
        val operand = _lastFactor ?: throw Error("Expected expression.")
        return UnaryOperatorNode(TokenType.Factorial, operand)
    }

    /**
     * Parse a unary negation operation. Unary negation can be defined as:
     * ```
     * UnaryNegation -> -Expr
     * ```
     *
     * @return A subtree representing the negation of an expression.
     */
    private fun parseNegative(): BinaryOperatorNode =
        BinaryOperatorNode(
            operator = TokenType.Asterisk,
            leftOperand = _negativeOne,
            rightOperand = parseFactor()
        )


    private fun parseLogicalNegation(): UnaryOperatorNode =
        UnaryOperatorNode(
            operator = TokenType.LogicalNegation,
            operand = parseFactor()
        )

    /**
     * Parse an expression inside parentheses.
     *
     * @return A parsed nested expression.
     */
    private fun parseParentheses(): ParseTreeNode {
        val expression = parseExpression()
        if (!_hasNext || peek()!!.type != TokenType.RightParenthesis) {
            throw Error("Expected: )")
        }
        advance()
        return expression
    }

    /**
     * Parse an expression (parse) tree from the token stream.
     *
     * @return An expression tree representing an expression.
     *
     * @see Parser For information on how to create a parser for
     * a token stream.
     */
    fun parse(): ExpressionTree {
        val root = parseExpression()
        return ExpressionTree(root)
    }
}