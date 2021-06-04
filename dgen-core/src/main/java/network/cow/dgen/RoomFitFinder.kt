package network.cow.dgen

import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.Transform

/**
 * TODO
 * algo to get all fits between two rooms
 *
 * @author Tobias Büser
 */
class RoomFitFinder {

    fun findFits(source: RoomBlueprint, doorId: Int, possibleFits: List<RoomBlueprint>): List<Fit> {
        // we want to find all blueprints (and their transformations)
        // that can fit at source's door with doorId
        val fits = mutableListOf<Fit>()
        possibleFits.forEach { fits.addAll(findFits(source, doorId, it)) }
        return fits
    }

    fun findFits(source: RoomBlueprint, doorId: Int, other: RoomBlueprint): List<Fit> {
        val door = source.doors[doorId]
        val outerPoint = door.adjacentVectors().firstOrNull {
            it !in source.outline
        } ?: return emptyList()

        val possibleFits = mutableListOf<Fit>()
        other.allowedTransforms.forEach { transform ->
            val otherTrans = other.transform(transform)

            otherTrans.doors.forEachIndexed { index, otherDoor ->
                // move other to passage point, so that otherDoor is exactly next to door
                val distance = outerPoint - otherDoor
                val otherShifted = otherTrans.shift(distance)

                if (!source.outline.overlapsWith(otherShifted.outline)) {
                    possibleFits.add(Fit(other, transform, DoorConnection(source.name, doorId, other.name, index)))
                }
            }
        }

        return possibleFits
    }

    data class Fit(
        val other: RoomBlueprint,
        val transform: Transform,
        val connection: DoorConnection
    )

    data class DoorConnection(
        val fromName: String,
        val fromDoorId: Int,
        val toName: String,
        val toDoorId: Int
    )

}
