package parsing

import math.factorial
import tokenization.Token
import tokenization.TokenType

/**
 * A class used to compute values from a parse/expression tree.
 */
data class ExpressionTree(val root: ParseTreeNode) {
    /**
     * Evaluate the expression tree.
     */
    fun evaluate() = evaluate(root)

    /**
     * Evaluate an expression from the root node of the parse tree.
     *
     * @param node The root node of the parse/expression tree.
     * @return The result of computing the parse/expression tree.
     */
    private fun evaluate(node: ParseTreeNode): ExpressionResult {
        return when (node) {
            is BinaryOperatorNode -> evaluateBinary(node)
            is UnaryOperatorNode -> evaluateUnary(node)
            is TerminalNode -> parseExpressionResult(node.value)
            else -> throw Error("Invalid parse tree node: $node")
        }
    }

    /**
     * Parses a value token into an `ExpressionResult`.
     *
     * @param valueToken The token to convert to an `ExpressionResult`.
     * @return An `ExpressionResult` representing the result of a computation.
     */
    private fun parseExpressionResult(valueToken: Token): ExpressionResult {
        return when (valueToken.value) {
            is Double -> ExpressionResult(valueToken.value as Double)
            is Boolean -> ExpressionResult(valueToken.value as Boolean)
            else -> throw Error("Invalid object type: ${valueToken.type}")
        }
    }

    private fun computeBinaryArithmetic(node: BinaryOperatorNode): ExpressionResult {
        val left = evaluate(node.leftOperand).value as Double
        val right = evaluate(node.rightOperand).value as Double

        return when (node.operator) {
            TokenType.Plus -> ExpressionResult(left + right)
            TokenType.Minus -> ExpressionResult(left - right)
            TokenType.Asterisk -> ExpressionResult(left * right)
            TokenType.Slash -> ExpressionResult(left / right)
            else -> throw Error("Invalid operands for arithmetic expression")
        }
    }

    private fun computeBinaryLogical(node: BinaryOperatorNode): ExpressionResult {
        val left = evaluate(node.leftOperand).value as Boolean
        val right = evaluate(node.rightOperand).value as Boolean

        return when (node.operator) {
            TokenType.Conjunction -> ExpressionResult(left && right)
            TokenType.Disjunction -> ExpressionResult(left || right)
            else -> throw Error("Invalid operands for logical expression")
        }
    }

    /**
     * Evaluates a binary operation.
     */
    private fun evaluateBinary(node: BinaryOperatorNode): ExpressionResult {
        return when (node.operator) {
            in TokenType.mathOperators -> computeBinaryArithmetic(node)
            in TokenType.logicalOperators -> computeBinaryLogical(node)
            else -> throw Error()
        }
    }

    private fun evaluateUnary(node: UnaryOperatorNode): ExpressionResult {
        when (node.operator) {
            TokenType.Factorial -> {
                val numberValue = evaluate(node.operand).value as Double
                val result = factorial(numberValue.toInt())
                return ExpressionResult(result.toDouble())
            }
            TokenType.LogicalNegation -> {
                val booleanValue = evaluate(node.operand).value as Boolean
                val result = !booleanValue
                return ExpressionResult(result)
            }
            else -> throw Error()
        }
    }
}