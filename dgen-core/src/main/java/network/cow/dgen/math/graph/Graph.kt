package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
interface Graph<V, E> {

    val vertices: Collection<V>
    val vertexKeys: Set<String>
    val edges: Collection<Edge<E>>

    val size: Int
        get() = vertices.size

    fun getVertex(key: String): V?
    fun getEdge(fromTo: Pair<String, String>): Edge<E>?
    fun getEdge(from: String, to: String) = this.getEdge(from to to)

    fun getEdgesTo(from: String): List<Edge<E>>
    fun getEdgesFrom(to: String): List<Edge<E>>

    fun shortestDistance(from: String, to: String): Int
    fun getNeighbors(vertex: String): Set<String>

    fun isPlanar(): Boolean
    fun isConnected(): Boolean

    data class Edge<E>(
        val from: String,
        val to: String,
        val descriptor: E
    )

}
