package network.cow.dgen.topology

/**
 * @author Tobias Büser
 */
interface Decomposer<T : Topology> {

    fun decompose(topology: T): List<Topology.Chain>

}
