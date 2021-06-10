package network.cow.dgen.math

/**
 * @author Tobias Büser
 */
interface Graph<V> {

    val vertices: Set<V>
    val edges: Set<Edge<V>>

    fun getVertex(key: String): V?
    fun getEdge(sourceKey: String, targetKey: String): Edge<V>?

    fun getEdgesTo(sourceKey: String): List<Edge<V>>
    fun getEdgesFrom(targetKey: String): List<Edge<V>>

    fun shortestDistance(sourceKey: String, targetKey: String): Int
    fun getNeighbors(vertex: String): Set<V>

    fun size() = this.vertices.size
    fun isPlanar(): Boolean
    fun isConnected(): Boolean

    data class Edge<V>(
        val sourceKey: String,
        val targetKey: String
    )

}
