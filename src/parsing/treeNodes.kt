package parsing

import tokenization.Token
import tokenization.TokenType

interface ParseTreeNode


internal data class TerminalNode(
    val value: Token
) : ParseTreeNode


internal data class BinaryOperatorNode(
    val operator: TokenType,
    val leftOperand: ParseTreeNode,
    val rightOperand: ParseTreeNode
) : ParseTreeNode


internal data class UnaryOperatorNode(
    val operator: TokenType,
    val operand: ParseTreeNode
) : ParseTreeNode