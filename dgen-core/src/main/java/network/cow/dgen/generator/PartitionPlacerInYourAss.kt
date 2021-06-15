package network.cow.dgen.generator

import network.cow.dgen.blueprint.MutatedRoomBlueprint
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.Transform
import network.cow.dgen.math.graph.Graph
import network.cow.dgen.math.graph.OrderedPartition
import java.util.UUID

class PartitionPlacerInYourAss(
    val graph: Graph<*, *>,
    val partition: OrderedPartition,
    val blueprints: List<RoomBlueprint>
) {

    private val structure = DungeonRoomStructure()
    private val placedStack = ArrayDeque<MutatedRoomBlueprint>()
    private val usedBlueprints = mutableMapOf<Int, MutableList<String>>()

    fun doItDaddy() {
        // if the partition is empty or if there are no
        // blueprints available, we can just return.
        if (partition.isEmpty() || blueprints.isEmpty()) return

        var currentId = 0

        // as long as we did not place as many rooms
        // as the partition tells us to, loop ..
        while (placedStack.size < partition.size) {
            // get current vertex from partition
            val vertex = partition[currentId]
            val possibleBlueprints = this.getPossibleBlueprintsForVertex(vertex)
            val blueprint = possibleBlueprints.randomOrNull() ?: return

            // the blueprint has to fit with all of his neighbors
            // that are already placed in the structure
            val neighbors = graph.getNeighbors(vertex).filter { it in this.structure }.map { this.structure.getVertex(it)!! }

            // TODO
            // fit this blueprint to be connected to his neighbors
            // and that it does not overlap with any other room
            val mutatedBlueprint = this.fitWith(blueprint, neighbors, this.structure)!!


            // it worked
            placedStack.addFirst(mutatedBlueprint)
            val alreadyUsed = usedBlueprints[currentId] ?: mutableListOf()
            alreadyUsed.add(blueprint.name)
            usedBlueprints[currentId] = alreadyUsed

            currentId++
        }

    }

    private fun fitWith(
        blueprint: RoomBlueprint,
        neighbors: List<MutatedRoomBlueprint>,
        structure: DungeonRoomStructure
    ): MutatedRoomBlueprint? {
        // if there are no neighbors, we can just place it and be done with it
        if (neighbors.isEmpty()) return MutatedRoomBlueprint(randomId(), blueprint)
        val neighborMap = neighbors.associateBy { it.id }

        // get all free doors from the given neighbors
        val freeDoorsByNeighbor = neighborMap.keys.associateWith { structure.getFreeDoors(it).toMutableList() }
        val bitchCock = freeDoorsByNeighbor.flatMap { entry ->
            entry.value.map { FreeDoor(entry.key, it) }
        }

        // we need all permutations of this form:
        // (room0:door0, room1:door1, ..., roomN:doorN, transformationN)
        val permutationSize = freeDoorsByNeighbor.values.fold(1) { prod, element -> prod * element.size } * Transform.values().size
        println("We need to calculate $permutationSize permutations.")

        // TODO
        // try to connect the blueprint to any freeDoors of the neighbors
        // without overlapping with any room in the structure
        return null
    }

    private fun getPossibleBlueprintsForVertex(vertex: String): List<RoomBlueprint> {
        // we first need to get the constraints for this room
        val doorSize = graph.getNeighbors(vertex).size

        return blueprints.filter { it.doorCount == doorSize }
    }

    private fun randomId() = UUID.randomUUID().toString().take(8)

    data class FreeDoor(val roomId: String, val doorId: Int)

    data class Permutation(val list: List<FreeDoor>, val transform: Transform)

}
