package network.cow.dgen.math

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author Tobias Büser
 */
open class Vector2D(val x: Double, val y: Double) {

    companion object {
        val ZERO = Vector2D(0.0, 0.0)
    }

    val magnitude = sqrt(this.x * this.x + this.y * this.y)

    operator fun plus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    operator fun plus(value: Double) = Vector2D(this.x + value, this.y + value)

    operator fun minus(other: Vector2D) = Vector2D(this.x - other.x, this.y - other.y)
    operator fun minus(value: Double) = Vector2D(this.x - value, this.y - value)

    operator fun times(other: Vector2D) = Vector2D(this.x * other.x, this.y * other.y)
    operator fun times(value: Double) = Vector2D(this.x * value, this.y * value)

    operator fun div(other: Vector2D) = Vector2D(this.x / other.x, this.y / other.y)
    operator fun div(value: Double) = Vector2D(this.x / value, this.y / value)

    fun rotate(degrees: Double, clockwise: Boolean = false): Vector2D {
        val theta = if (clockwise) 360 - degrees else degrees

        return Vector2D(
            this.x * betterCos(theta) - this.y * betterSin(theta),
            this.x * betterSin(theta) + this.y * betterCos(theta)
        )
    }

    fun distanceTo(other: Vector2D) = sqrt((this.x - other.x).pow(2) + (this.y - other.y).pow(2))
    fun isNextTo(other: Vector2D, delta: Double = 1.0) = this.distanceTo(other) <= delta

    fun adjacentPoints(offset: Double = 1.0): List<Vector2D> {
        return listOf(
            Vector2D(this.x + offset, this.y),
            Vector2D(this.x - offset, this.y),
            Vector2D(this.x, this.y + offset),
            Vector2D(this.x, this.y - offset),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Vector2D

        if (x != other.x) return false
        if (y != other.y) return false
        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    override fun toString(): String {
        return "Vector2D(x=$x, y=$y)"
    }

}
