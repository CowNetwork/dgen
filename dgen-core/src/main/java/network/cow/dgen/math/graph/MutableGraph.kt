package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
interface MutableGraph<V, E : Flippable<E>> : Graph<V, E> {

    fun addVertex(key: String, vertex: V)

    fun addEdge(edge: Graph.Edge<E>)
    fun addEdge(fromTo: Pair<String, String>, descriptor: E) = this.addEdge(Graph.Edge(fromTo.first, fromTo.second, descriptor))
    fun addEdge(from: String, to: String, descriptor: E) = this.addEdge(from to to, descriptor)

    fun removeVertex(vertex: String)
    fun removeVertices(vararg vertices: String) = vertices.forEach { removeVertex(it) }

    fun removeEdge(from: String, to: String)
    fun removeEdges(filter: (Graph.Edge<E>) -> Boolean)

    fun removeEdgesFrom(from: String) = this.removeEdges { it.from == from }
    fun removeEdgesTo(to: String) = this.removeEdges { it.to == to }

    fun deepCopy(): MutableGraph<V, E>

}
