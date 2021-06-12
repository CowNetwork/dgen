package network.cow.dgen.generator

import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.MutatedRoomBlueprint
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.Vector2D
import network.cow.dgen.math.graph.BreadthFirstSearchGraphDecomposer
import network.cow.dgen.math.graph.Graph
import network.cow.dgen.topology.Topology

/**
 * @author Tobias Büser
 */
class TopologyDungeonGenerator(
    val topology: Topology,
    seed: Long, blueprints: List<RoomBlueprint>,
    options: Options
) : DungeonGenerator(seed, blueprints, options) {

    /*

    TODO
    now we have the partitions of the topology, that we want to generate
    seperatedly.
    SO we need an algorithm that just works with one partition (i.e. a specific order
    of vertices) and connects them together.


    We need:
    - list of partitions
    - stack with already placed rooms
    - map with used blueprints for specific rooms
    - `MutatedRoomBlueprint` which holds the transformation and shifting
    - after placing first room, get the second vertex from the partition and check where
    he needs to be placed. the partition is ordered in such a way, that the next room will
    have a room where it can be placed to. then filter the blueprints cause we know what
    kind of room the next vertex is

    */
    override fun generate(): List<DungeonRoom> {
        val filteredBlueprints = this.filterBlueprints(blueprints, topology)
        println("FilteredBlueprints(${filteredBlueprints.size}/${blueprints.size}):")
        println("=> ${filteredBlueprints.map { it.name + "[" + it.doorCount + "]" }}")
        println(" ")

        val decomposer = BreadthFirstSearchGraphDecomposer()
        val partitions = decomposer.decompose(topology)
        println("Decomposed partitions:")
        partitions.forEach {
            println("=> $it")
        }
        println("")

        val partitionPlacer = PartitionPlacerInYourAss(topology, partitions[0], filteredBlueprints)
        partitionPlacer.doItDaddy()

        return emptyList()
    }

    // check if we have fitting blueprints for the topology
    // filter out all, that we dont need, i.e.:
    // - all that has less or more door counts that we need
    // - OR all that doesnt have at least one vertex having a constraint with it
    fun filterBlueprints(blueprints: List<RoomBlueprint>, topology: Topology): List<RoomBlueprint> {
        val neededDoorCounts = topology.vertexKeys.map {
            topology.getNeighbors(it).size
        }.toSet()

        neededDoorCounts.forEach {
            val fittingCount = blueprints.count { blueprint -> blueprint.doors.size == it }
            if (fittingCount == 0) {
                // throw error
                throw IllegalArgumentException("There needs to be at least one blueprint with door size == $it")
            }
        }

        return blueprints.filter { it.doors.size in neededDoorCounts }
    }

    class PartitionPlacerInYourAss(
        val graph: Graph<*, *>,
        val partition: Graph.Partition,
        val blueprints: List<RoomBlueprint>
    ) {

        // TODO
        /*
        - rename source-origin to to-from
        - add <E> for the edge type
        - add Pair(from, to) as key to the edges
        -> all to the Graph<V, E> class
         */

        // TODO is not enough
        // we need the information, how the rooms are connected, maybe through
        // a `RoomStructure`
        private val placedStack = ArrayDeque<MutatedRoomBlueprint>()
        private val usedBlueprints = mutableListOf<MutableList<String>>()

        fun doItDaddy() {
            if (partition.isEmpty() || blueprints.isEmpty()) return

            var currentId = 0

            while (placedStack.size < partition.size) {
                // get vertex from partition
                val vertex = partition[currentId]
                val blueprint = this.getBlueprintForVertex(vertex) ?: return

                // try to fit this blueprint to the already created room structure
                val mutatedBlueprint = MutatedRoomBlueprint(blueprint, listOf(), Vector2D.ZERO)
                // TODO

                // it worked
                placedStack.addFirst(mutatedBlueprint)
                val alreadyUsed = usedBlueprints.getOrNull(currentId) ?: mutableListOf()
                alreadyUsed.add(blueprint.name)
                usedBlueprints[currentId] = alreadyUsed

                currentId++
            }

        }

        private fun getBlueprintForVertex(vertex: String): RoomBlueprint? {
            // we first need to get the constraints for this room
            val doorSize = graph.getNeighbors(vertex).size

            return blueprints.filter { it.doorCount == doorSize }.randomOrNull()
        }

    }

}
