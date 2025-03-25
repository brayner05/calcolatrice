package tokenization

enum class TokenType {
    // Arithmetic Operators
    Plus, Minus, Asterisk, Slash, Caret,
    Factorial,

    // Logical Operators
    Conjunction, Disjunction, LogicalNegation,
    Implication, BiCondition,

    // Equality Operators
    EqualEqual, LessThan, LessOrEqual,
    GreaterThan, GreaterOrEqual, BangEqual,


    // Brackets
    LeftParenthesis, RightParenthesis,

    // Literals
    Number, Boolean,

    // Miscellaneous
    EndOfFile
}
