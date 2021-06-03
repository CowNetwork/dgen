package network.cow.dgen.math

import network.cow.dgen.math.MathHelpers.betterCos
import network.cow.dgen.math.MathHelpers.betterSin
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @author Tobias Büser
 */
open class Vector2D(val x: Double, val y: Double) {

    companion object {
        val ZERO = Vector2D(0.0, 0.0)
    }

    /**
     * Represents the magnitude of this vector, which is commonly
     * known as the length in euclidian spaces.
     */
    val magnitude = sqrt(this.x * this.x + this.y * this.y)

    operator fun plus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    operator fun plus(value: Double) = Vector2D(this.x + value, this.y + value)

    operator fun minus(other: Vector2D) = Vector2D(this.x - other.x, this.y - other.y)
    operator fun minus(value: Double) = Vector2D(this.x - value, this.y - value)

    operator fun times(other: Vector2D) = Vector2D(this.x * other.x, this.y * other.y)
    operator fun times(value: Double) = Vector2D(this.x * value, this.y * value)

    operator fun div(other: Vector2D) = Vector2D(this.x / other.x, this.y / other.y)
    operator fun div(value: Double) = Vector2D(this.x / value, this.y / value)

    operator fun component1() = this.x
    operator fun component2() = this.y

    /**
     * Rotates this vector by [degrees]° clockwise.
     */
    fun rotate(degrees: Double): Vector2D {
        return Vector2D(
            this.x * betterCos(degrees) + this.y * betterSin(degrees),
            -this.x * betterSin(degrees) + this.y * betterCos(degrees)
        )
    }

    fun transform(transformation: Transformation): Vector2D {
        return when (transformation) {
            Transformation.IDENTITY -> Vector2D(this.x, this.y)
            Transformation.ROTATE90 -> this.rotate(90.0)
            Transformation.ROTATE180 -> this.rotate(180.0)
            Transformation.ROTATE270 -> this.rotate(270.0)
            Transformation.MIRRORX -> Vector2D(this.x, -this.y)
            Transformation.MIRRORY -> Vector2D(-this.x, this.y)
            Transformation.FLIP13 -> Vector2D(this.y, this.x)
            Transformation.FLIP24 -> Vector2D(-this.y, -this.x)
        }
    }

    /**
     * Returns the euclidean distance between this and [other].
     */
    fun distanceTo(other: Vector2D) = (this - other).magnitude

    /**
     * Returns the manhattan distance between this and [other].
     */
    fun manhattanDistanceTo(other: Vector2D) = abs(this.x - other.x) + abs(this.y - other.y)

    /**
     * Checks if vector [other] is in distance <= [delta] to this.
     */
    fun isNextTo(other: Vector2D, delta: Double = 1.0) = this.distanceTo(other) <= delta

    /**
     * Returns a list of all vectors, that are exactly at a [offset]
     * distance to this vector, but only horizontally/vertically.
     */
    fun adjacentVectors(offset: Double = 1.0): List<Vector2D> {
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
        return "Vector($x, $y)"
    }

}
