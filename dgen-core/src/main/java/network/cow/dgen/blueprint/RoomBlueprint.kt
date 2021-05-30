package network.cow.dgen.blueprint

import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D

/**
 * @author Tobias Büser
 */
abstract class RoomBlueprint(
    val name: String, val outline: Polygon2D,
    val passagePoints: List<Vector2D>, rotation: Float = 0f
) {

    val rotation = rotation; get() = field % MAX_ROTATION

    companion object {
        private const val MAX_ROTATION = 360
        private val ROTATION_DEGREES = listOf(0f, 90f, 180f, 270f)
    }

    init {
        if (passagePoints.isEmpty()) throw IllegalArgumentException("A room should always contain a passage point.")

        val noDuplicates = this.outline.vertices.all {
            this.outline.vertices.count { other -> it == other } == 1
        }
        if (!noDuplicates) throw IllegalArgumentException("All vertices have to be unique.")

        val straightLines = this.outline.sides.all {
            it.isVertical || it.isHorizontal
        }
        if (!straightLines) throw IllegalArgumentException("The vertices need to form only straight lines.")

        val passagesOnOutline = this.passagePoints.all {
            this.outline.sides.count { side -> it in side } == 1
        }
        if (!passagesOnOutline) throw IllegalArgumentException("All passage points need to be exactly on one side of the outline.")

        val passagesNotAdjacent = this.passagePoints.all {
            !passagePoints.any { other -> it != other && it.isNextTo(other) }
        }
        if (!passagesNotAdjacent) throw IllegalArgumentException("Two passage points can not be adjacent.")
    }

    /**
     * Rotates the blueprint and its outline and passage points
     * [degrees]° around the origin [Vector2D.ZERO].
     *
     * Default for mathematic operations is counterclockwise, but
     * clockwise is more intuitive, that's why its the default.
     */
    abstract fun rotate(degrees: Float, clockwise: Boolean = true): RoomBlueprint

    /**
     * Shifts the whole blueprint by the given vector.
     */
    abstract fun shift(by: Vector2D): RoomBlueprint

    /**
     * Normalizes the blueprint by shifting it to the origin (0,0).
     *
     * That is especially useful when you want to have relative coordinates
     * instead of absolute.
     */
    fun normalize() = this.shift(Vector2D.ZERO - this.outline.min)

    fun findAllFits(otherBlueprints: List<RoomBlueprint>, passagePoint: Vector2D): List<PossibleFit> {
        val possibleFits = mutableListOf<PossibleFit>()
        otherBlueprints.forEach {
            possibleFits.addAll(this.findFitsWith(it, passagePoint))
        }
        return possibleFits
    }

    /**
     * Tries out every possible combination of rotating [other] and shifting it
     * to every [passagePoints] and returns every combination that fits.
     * Fitting means, that other connects correctly to the passage point without intersecting
     * any line of this blueprint.
     */
    fun findFitsWith(other: RoomBlueprint, passagePoint: Vector2D): List<PossibleFit> {
        val outerPoint = passagePoint.adjacentPoints().firstOrNull {
            it !in this.outline
        } ?: return emptyList()

        val passageIndex = this.passagePoints.indexOf(passagePoint)
        val possibleFits = mutableListOf<PossibleFit>()

        ROTATION_DEGREES.forEach { degrees ->
            val otherRotated = other.rotate(degrees, true)

            otherRotated.passagePoints.forEachIndexed { index, otherPassage ->
                // move other to passage point, so that otherPassage is exactly next to passagePoint
                val distance = outerPoint - otherPassage
                val otherShifted = otherRotated.shift(distance)

                if (!this.outline.overlapsWith(otherShifted.outline)) {
                    possibleFits.add(PossibleFit(passageIndex, index, otherShifted))
                }
            }
        }
        return possibleFits
    }

    /**
     * Represents a fit between this and [other], where
     * the passage point of this with index [index] fits to other
     * passage point [otherIndex].
     */
    data class PossibleFit(
        val index: Int,
        val otherIndex: Int, val other: RoomBlueprint
    )

}
