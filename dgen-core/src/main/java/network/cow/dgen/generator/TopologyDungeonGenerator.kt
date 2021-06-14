package network.cow.dgen.generator

import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.MutatedRoomBlueprint
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.graph.BreadthFirstSearchGraphDecomposer
import network.cow.dgen.math.graph.Graph
import network.cow.dgen.math.graph.OrderedPartition
import network.cow.dgen.topology.Topology
import java.util.UUID

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
        val partitions = decomposer.decompose(topology.graph)
        println("Decomposed partitions:")
        partitions.forEach {
            println("=> $it")
        }
        println("")

        val partitionPlacer = PartitionPlacerInYourAss(topology.graph, partitions[0], filteredBlueprints)
        partitionPlacer.doItDaddy()

        return emptyList()
    }

    // check if we have fitting blueprints for the topology
    // filter out all, that we dont need, i.e.:
    // - all that has less or more door counts that we need
    // - OR all that doesnt have at least one vertex having a constraint with it
    fun filterBlueprints(blueprints: List<RoomBlueprint>, topology: Topology): List<RoomBlueprint> {
        val neededDoorCounts = topology.graph.vertexKeys.map {
            topology.graph.getNeighbors(it).size
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
                val mutatedBlueprint = this.fitWith(vertex, blueprint, neighbors, this.structure)!!


                // it worked
                placedStack.addFirst(mutatedBlueprint)
                val alreadyUsed = usedBlueprints[currentId] ?: mutableListOf()
                alreadyUsed.add(blueprint.name)
                usedBlueprints[currentId] = alreadyUsed

                currentId++
            }

        }

        private fun fitWith(
            vertex: String,
            blueprint: RoomBlueprint,
            neighbors: List<MutatedRoomBlueprint>,
            structure: DungeonRoomStructure
        ): MutatedRoomBlueprint? {
            // if there are no neighbors, we can just place it and be done with it
            if (neighbors.isEmpty()) return MutatedRoomBlueprint(randomId(), blueprint)

            // get all free doors from the given neighbors
            val freeDoorsByNeighbor = neighbors.map { it.id }.associateWith { structure.getFreeDoors(it) }

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

    }

}
