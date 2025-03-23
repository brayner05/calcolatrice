package tokenization

enum class TokenType {
    // Operators
    Plus, Minus, Asterisk, Slash,
    Caret, Function,

    // Brackets
    LeftParenthesis, RightParenthesis,

    // Types
    Number, Vector,

    // Miscellaneous
    EndOfFile
}
