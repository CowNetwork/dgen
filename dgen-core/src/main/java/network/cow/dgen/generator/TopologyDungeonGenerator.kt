package network.cow.dgen.generator

import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.graph.BreadthFirstSearchGraphDecomposer
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

}
