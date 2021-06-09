package network.cow.dgen

import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.generator.DungeonGenerator
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

    How does the generation work?
    1 Choose initial vertex
    2 Choose a room that fits to this room
    3 If no room fits, go back to the initial step and choose another initial room (if there is any left)
    4 If a room fits, set the new room to this room and do step 1 again.

    WITHOUT BACKTRACKING:
    1. current room and try to find a fit for that
    2. if no fit found: break;
    3. if fit found: add to room and repeat with 1.

    WITH BACKTRACKING:
    - we need a way to keep track on what path we are and what paths
    we already tried.
    - a path is defined like so:
    - spawn001 ==0=> normal001 ==0=> final001
    - BUT that only works with single paths ...

    */
    override fun generate(): List<DungeonRoom> {
        this.checkTopology(topology)
        val filteredBlueprints = this.filterBlueprints(blueprints, topology)

        println("=> FilteredBlueprints: ${filteredBlueprints.size}/${blueprints.size}")
        println("=> ${filteredBlueprints.map { it.name + "[" + it.doors.size + "]" }}")

        return emptyList()
    }

    // check if the topology is correct
    fun checkTopology(topology: Topology) {
        if (topology.size() <= 1) throw IllegalArgumentException("The topology needs at least two rooms.")
        if (!topology.isConnected()) throw IllegalArgumentException("The topology should not contain unreachable rooms.")
        if (!topology.isPlanar()) throw IllegalArgumentException("The underlying graph needs to be planar")
    }

    // check if we have fitting blueprints for the topology
    // filter out all, that we dont need, i.e.:
    // - all that has less or more door counts that we need
    // - OR all that doesnt have at least one vertex having a constraint with it
    fun filterBlueprints(blueprints: List<RoomBlueprint>, topology: Topology): List<RoomBlueprint> {
        val neededDoorCounts = topology.vertices.map {
            topology.getNeighbors(it.element).size
        }.toSet()

        neededDoorCounts.forEach {
            val fittingCount = blueprints.count { blueprint -> blueprint.doors.size == it }
            if (fittingCount == 0) {
                // throw error
                throw IllegalArgumentException("There needs to be at least one blueprint with door size == $it")
            }
        }

        return blueprints.filter { it.doors.size in neededDoorCounts }
            .filter { topology.vertices.any { vert -> vert.constraint.test(it) } }
    }

}
