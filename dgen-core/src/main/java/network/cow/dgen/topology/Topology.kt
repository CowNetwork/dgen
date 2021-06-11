package network.cow.dgen.topology

import network.cow.dgen.math.graph.Graph
import network.cow.dgen.math.graph.JGraphMutableGraph

/**
 * @author Tobias Büser
 */
class Topology(
    vertices: Map<String, Node> = mapOf(),
    edges: Set<Graph.Edge> = setOf()
) : JGraphMutableGraph<Node>(vertices, edges) {

    init {
        if (this.size <= 1) throw IllegalArgumentException("The topology needs at least two rooms.")
        if (!this.isConnected()) throw IllegalArgumentException("The topology should not contain unreachable rooms.")
        if (!this.isPlanar()) throw IllegalArgumentException("The underlying graph needs to be planar")
    }

}

typealias Node = Int
