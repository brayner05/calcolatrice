package parsing

import math.factorial
import tokenization.TokenType

data class ExpressionTree(val root: ParseTreeNode) {
    fun evaluate() = evaluate(root)

    private fun evaluate(node: ParseTreeNode): ExpressionResult {
        return when (node) {
            is BinaryOperatorNode -> evaluateBinary(node)
            is UnaryOperatorNode -> evaluateUnary(node)
            is TerminalNode -> ExpressionResult(node.value.value as Double)
            else -> throw Error()
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
            else -> throw Error()
        }
    }
}