package parsing

import tokenization.TokenType

data class ExpressionTree(val root: ParseTreeNode) {
    fun evaluate(): ExpressionResult {
        var node = root
        return evaluate(root)
    }

    private fun evaluate(node: ParseTreeNode): ExpressionResult {
        return when (node) {
            is BinaryOperatorNode -> evaluateBinary(node)
            is UnaryOperatorNode -> throw Error()
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
}