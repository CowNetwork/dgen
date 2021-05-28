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

    abstract fun rotate(degrees: Float, clockwise: Boolean = true): RoomBlueprint

    abstract fun shift(by: Vector2D): RoomBlueprint

    fun findAllFits(otherBlueprints: List<RoomBlueprint>, passagePoint: Vector2D): List<PossibleFit> {
        val possibleFits = mutableListOf<PossibleFit>()
        otherBlueprints.forEach {
            possibleFits.addAll(this.findFits(it, passagePoint))
        }
        return possibleFits
    }

    fun findFits(other: RoomBlueprint, passagePoint: Vector2D): List<PossibleFit> {
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

    data class PossibleFit(
        val index: Int,
        val otherIndex: Int, val blueprint: RoomBlueprint
    )

}
