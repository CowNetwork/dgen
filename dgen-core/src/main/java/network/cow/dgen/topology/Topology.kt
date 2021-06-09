package network.cow.dgen.topology

import network.cow.dgen.blueprint.RoomBlueprint
import java.util.function.Predicate

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

    fun getVertex(element: String): Vertex?
    fun getEdge(source: String, target: String): Edge?

    fun shortestDistance(source: String, dest: String): Int
    fun getNeighbors(vertex: String): Set<Vertex>

    fun size() = this.vertices.size
    fun isPlanar(): Boolean
    fun isConnected(): Boolean

    data class Vertex(val element: String, val constraint: Predicate<RoomBlueprint> = Predicate { true })
    data class Edge(val source: String, val target: String)
    data class Chain(val vertices: List<Vertex>) : ArrayList<Vertex>(vertices)

}
