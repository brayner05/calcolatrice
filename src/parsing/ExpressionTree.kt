package parsing

import math.factorial
import tokenization.Token
import tokenization.TokenType

data class ExpressionTree(val root: ParseTreeNode) {
    fun evaluate() = evaluate(root)

    private fun evaluate(node: ParseTreeNode): ExpressionResult {
        return when (node) {
            is BinaryOperatorNode -> evaluateBinary(node)
            is UnaryOperatorNode -> evaluateUnary(node)
            is TerminalNode -> parseExpressionResult(node.value)
            else -> throw Error("Invalid parse tree node: $node")
        }
    }

    private fun parseExpressionResult(valueToken: Token): ExpressionResult {
        return when (valueToken.value) {
            is Double -> ExpressionResult(valueToken.value as Double)
            is Boolean -> ExpressionResult(valueToken.value as Boolean)
            else -> throw Error("Invalid object type: ${valueToken.type}")
        }
    }

    private fun evaluateBinary(node: BinaryOperatorNode): ExpressionResult {
        val left = evaluate(node.leftOperand).value as Double
        val right = evaluate(node.rightOperand).value as Double

        return when (node.operator) {
            TokenType.Plus -> ExpressionResult(left + right)
            TokenType.Minus -> ExpressionResult(left - right)
            TokenType.Asterisk -> ExpressionResult(left * right)
            TokenType.Slash -> ExpressionResult(left / right)
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