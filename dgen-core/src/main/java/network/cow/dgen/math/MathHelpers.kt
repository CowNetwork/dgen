package network.cow.dgen.math

import kotlin.math.cos
import kotlin.math.sin

internal object MathHelpers {

    internal const val MAX_ROTATION = 360
    internal val ROTATION_DEGREES = listOf(0f, 90f, 180f, 270f)

    fun betterCos(degree: Double): Double {
        if (degree < 0) return -1 * betterCos(degree * -1)
        return when (degree % 360) {
            0.0 -> 1.0
            90.0 -> 0.0
            180.0 -> -1.0
            270.0 -> 0.0
            else -> cos(Math.toRadians(degree))
        }
    }

    fun betterSin(degree: Double): Double {
        if (degree < 0) return -1 * betterSin(degree * -1)
        return when (degree % 360) {
            0.0 -> 0.0
            90.0 -> 1.0
            180.0 -> 0.0
            270.0 -> -1.0
            else -> sin(Math.toRadians(degree))
        }
    }

}
