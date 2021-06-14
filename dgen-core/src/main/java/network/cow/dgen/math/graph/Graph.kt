package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
interface Graph<V, E : Flippable<E>> {

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
    fun getNeighboringEdges(vertex: String) = this.getEdgesTo(vertex) + this.getEdgesFrom(vertex)

    fun shortestDistance(from: String, to: String): Int
    fun getNeighbors(vertex: String): Set<String>

    fun isPlanar(): Boolean
    fun isConnected(): Boolean

    operator fun contains(vertex: String) = this.getVertex(vertex) != null

    /**
     * This class represents an undirected edge in a [Graph].
     *
     * We need to have it [Flippable], because at times, we want to
     * choose the perspective for the edge based on a vertex it connects
     * to.
     */
    data class Edge<E : Flippable<E>>(
        val from: String,
        val to: String,
        val descriptor: E
    ) : Flippable<Edge<E>> {

        /**
         * The perspective of an edge is the [from] vertex.
         * If the given [vertex] is not the current [from],
         * then we can flip the edge, so that the new [from] is
         * our current [to].
         *
         * That is useful when needing to observe a vertex and its edges
         * from the vertex's point of view.
         */
        fun setPerspective(vertex: String): Edge<E> {
            return when {
                from == vertex -> this
                to == vertex -> this.flip()
                else -> throw IllegalArgumentException("Can't set the perspective to a vertex this edge does not connect to. ($vertex)")
            }
        }

        override fun flip() = Edge(to, from, descriptor.flip())

    }

}
