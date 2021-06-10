package network.cow.dgen.topology

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph

fun DefaultUndirectedGraph<String, DefaultEdge>.copy(): DefaultUndirectedGraph<String, DefaultEdge> {
    val copy = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    this.vertexSet().forEach { copy.addVertex(it) }
    this.edgeSet().forEach { copy.addEdge(this.getEdgeSource(it), this.getEdgeTarget(it)) }

    return copy
}
