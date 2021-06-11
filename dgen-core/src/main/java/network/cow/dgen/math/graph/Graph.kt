package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
interface Graph<V> {

    val vertices: Collection<V>
    val vertexKeys: Set<String>
    val edges: Set<Edge>

    val size: Int
        get() = vertices.size

    fun getVertex(key: String): V?
    fun getEdge(sourceKey: String, targetKey: String): Edge?

    fun getEdgesTo(sourceKey: String): List<Edge>
    fun getEdgesFrom(targetKey: String): List<Edge>

    fun shortestDistance(sourceKey: String, targetKey: String): Int
    fun getNeighbors(vertexKey: String): Set<String>

    fun isPlanar(): Boolean
    fun isConnected(): Boolean

    data class Edge(
        val sourceKey: String,
        val targetKey: String
    )

    class DirectedChain<V>(vertices: List<V>) : ArrayList<V>(vertices) {

        /**
         * Two chains are next to each other, when at least
         * one vertex of this chain has a common edge with one vertex
         * of the other chain.
         */
        fun isNextTo(other: DirectedChain<*>): Boolean {
            return true
        }

    }

}
