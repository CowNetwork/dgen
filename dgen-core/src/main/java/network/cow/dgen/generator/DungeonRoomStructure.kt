package network.cow.dgen.generator

import network.cow.dgen.blueprint.MutatedRoomBlueprint
import network.cow.dgen.math.graph.Flippable
import network.cow.dgen.math.graph.JGraphMutableGraph

/**
 * @author Tobias Büser
 */
class DungeonRoomStructure : JGraphMutableGraph<MutatedRoomBlueprint, DungeonRoomPassage>() {

    fun getFreeDoors(vertex: String): List<Int> {
        val room = this.getVertex(vertex) ?: return emptyList()
        val connectedDoors = this.getNeighboringEdges(vertex)
            .map { it.setPerspective(vertex) }
            .associateBy { it.descriptor.firstDoorId }

        return room.blueprint.doors
            .filterIndexed { index, _ -> !connectedDoors.containsKey(index) }
            .mapIndexed { index, _ -> index }
    }

}

data class DungeonRoomPassage(
    val firstDoorId: Int,
    val secondDoorId: Int
) : Flippable<DungeonRoomPassage> {

    override fun flip() = DungeonRoomPassage(secondDoorId, firstDoorId)

}
