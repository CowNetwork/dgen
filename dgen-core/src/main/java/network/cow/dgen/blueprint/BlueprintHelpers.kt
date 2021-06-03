package network.cow.dgen.blueprint

import network.cow.dgen.math.MathHelpers.ROTATION_DEGREES
import network.cow.dgen.math.Vector2D

// TODO same as below.
fun RoomBlueprint.findAllFits(otherBlueprints: List<RoomBlueprint>, passagePoint: Vector2D): List<PossibleFit> {
    val possibleFits = mutableListOf<PossibleFit>()
    otherBlueprints.forEach {
        possibleFits.addAll(this.findFitsWith(it, passagePoint))
    }
    return possibleFits
}

/**
 * @author Tobias Büser
 */
/**
 * Tries out every possible combination of rotating [other] and shifting it
 * to every [door] and returns every combination that fits.
 * Fitting means, that other connects correctly to the doors without intersecting
 * any line of this blueprint.
 *
 * TODO this kind of logic shouldnt be in `RoomBlueprint`
 */
fun RoomBlueprint.findFitsWith(other: RoomBlueprint, door: Vector2D): List<PossibleFit> {
    val outerPoint = door.adjacentVectors().firstOrNull {
        it !in this.outline
    } ?: return emptyList()

    val passageIndex = this.doors.indexOf(door)
    val possibleFits = mutableListOf<PossibleFit>()

    ROTATION_DEGREES.forEach { degrees ->
        val otherRotated = other.rotate(degrees)

        otherRotated.doors.forEachIndexed { index, otherPassage ->
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
 * the door of this with index [index] fits to other
 * door+ [otherIndex].
 */
data class PossibleFit(
    val index: Int,
    val otherIndex: Int, val other: RoomBlueprint
)
