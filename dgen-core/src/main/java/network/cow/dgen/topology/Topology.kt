package network.cow.dgen.topology

/**
 * The input is a set of vertices and edges connecting them.
 * - check if the graph is planar
 * - get faces, i.e. List<List<Vertex>>
 *
 * @author Tobias Büser
 */
interface Topology {

    val vertices: Set<Vertex>
    val edges: Set<Edge>

    fun shortestDistance(source: String, dest: String): Int
    fun getNeighbors(vertex: String): Set<Vertex>

    fun size() = this.vertices.size
    fun isPlanar(): Boolean
    fun isConnected(): Boolean

    data class Vertex(val element: String)
    data class Edge(val source: String, val dest: String)

}
