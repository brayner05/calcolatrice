package tokenization

enum class TokenType {
    // Operators
    Plus, Minus, Asterisk, Slash,
    Caret, Factorial,

    // Brackets
    LeftParenthesis, RightParenthesis,

    // Types
    Number, Vector,

    // Miscellaneous
    EndOfFile
}
