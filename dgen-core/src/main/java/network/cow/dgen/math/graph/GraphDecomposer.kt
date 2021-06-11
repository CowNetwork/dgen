package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
interface GraphDecomposer {

    fun decompose(graph: MutableGraph<*>): List<Graph.DirectedChain<String>>

}
