package network.cow.dgen.topology

import network.cow.dgen.math.graph.Flippable
import network.cow.dgen.math.graph.Graph
import network.cow.dgen.math.graph.JGraphMutableGraph
import network.cow.dgen.math.graph.MutableGraph

/**
 * @author Tobias Büser
 */
class Topology(
    vertices: Map<String, Int> = mapOf(),
    edges: Set<Graph.Edge<Hure>> = setOf()
) {

    val graph: MutableGraph<Int, Hure> = JGraphMutableGraph(vertices, edges)

    init {
        if (graph.size <= 1) throw IllegalArgumentException("The topology needs at least two rooms.")
        if (!graph.isConnected()) throw IllegalArgumentException("The topology should not contain unreachable rooms.")
        if (!graph.isPlanar()) throw IllegalArgumentException("The underlying graph needs to be planar")
    }

}

class Hure(val i: Int) : Flippable<Hure> {
    override fun flip() = this

}
