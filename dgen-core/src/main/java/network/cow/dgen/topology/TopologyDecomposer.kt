package network.cow.dgen.topology

/**
 * @author Tobias Büser
 */
interface TopologyDecomposer {

    fun decompose(topology: Topology): List<Topology.Chain>

}
