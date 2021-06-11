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

    /**
     * A chunk is just a collection of vertices, that are
     * connected to each other. But the order of the list does not
     * correspond to the real connection between them!
     */
    class Chunk(vertices: List<String>) : ArrayList<String>(vertices) {

        /**
         * Two chunks are next to each other, when at least
         * one vertex of this chunks has a common edge in [graph]
         * with one vertex of the [other] chunks.
         */
        fun isNextTo(graph: Graph<*>, other: Chunk): Boolean {
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
