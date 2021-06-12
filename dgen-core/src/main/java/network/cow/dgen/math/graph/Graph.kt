package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
interface Graph<V, E> {

    val vertices: Collection<V>
    val vertexKeys: Set<String>
    val edges: Set<Edge<E>>

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

    /**
     * A partition is just a collection of vertices, that are
     * connected to each other. But the order of the list does not
     * correspond to the real connection between them!
     */
    class Partition(vertices: List<String>) : ArrayList<String>(vertices) {

        /**
         * Two partitions are next to each other, when at least
         * one vertex of this partition has a common edge in [graph]
         * with one vertex of the [other] partition.
         */
        fun isNextTo(graph: Graph<*, *>, other: Partition): Boolean {
            for (vertex in this) {
                val neighbors = graph.getNeighbors(vertex)
                for (otherVertex in other) {
                    if (otherVertex in neighbors) return true
                }
            }
            return false
        }

    }

}
