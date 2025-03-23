package parsing

class ExpressionResult {
    var value: Any? = null

    val isNumber: Boolean
        get() = value != null && value is Double

    val isBoolean: Boolean
        get() = value != null && value is Boolean

    constructor(value: Double) {
        this.value = value
    }

    constructor(value: Boolean) {
        this.value = value
    }
}