package math

import kotlin.math.sqrt

interface Vector {
    val magnitude: Double
    fun dot(v: Vector): Double
    operator fun plus(v: Vector): Vector
    operator fun minus(v: Vector): Vector
    operator fun times(scalar: Double): Vector
}


data class Vector2D(val x: Double, val y: Double) : Vector {
    override val magnitude: Double
        get() = sqrt(x * x + y * y)

    override fun dot(v: Vector): Double {
        if (v !is Vector2D) {
            throw IllegalArgumentException("Operand size/dimension mismatch")
        }
        return x * v.x + y * v.y
    }

    override fun plus(v: Vector): Vector {
        if (v !is Vector2D) {
            throw IllegalArgumentException("Operand size/dimension mismatch")
        }
        return Vector2D(x + v.x, y + v.y)
    }

    override fun minus(v: Vector): Vector {
        if (v !is Vector2D) {
            throw IllegalArgumentException("Operand size/dimension mismatch")
        }
        return Vector2D(x - v.x, y - v.y)
    }

    override fun times(scalar: Double) = Vector2D(scalar * x, scalar * y)
}