package network.cow.dgen.topology

import org.jgrapht.Graph
import org.jgrapht.Graphs
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph

/**
 * @author Tobias Büser
 */
class JGraphTopology(
    override val vertices: Set<Topology.Vertex>,
    override val edges: Set<Topology.Edge>
) : Topology {

    private val vertexMap = vertices.associateBy { it.element }
    private val edgeMap = edges.associateBy { it.source to it.target }

    private val graph: Graph<String, DefaultEdge>
    private val planarityInspector: BoyerMyrvoldPlanarityInspector<String, DefaultEdge>
    private val connectivityInspector: ConnectivityInspector<String, DefaultEdge>

    init {
        graph = DefaultUndirectedGraph(DefaultEdge::class.java)

        vertices.forEach { graph.addVertex(it.element) }
        edges.forEach { graph.addEdge(it.source, it.target) }

        this.planarityInspector = BoyerMyrvoldPlanarityInspector(graph)
        this.connectivityInspector = ConnectivityInspector(graph)
    }

    override fun getVertex(element: String) = this.vertexMap[element]
    override fun getEdge(source: String, target: String) = this.edgeMap[source to target]

    override fun shortestDistance(source: String, dest: String): Int {
        return DijkstraShortestPath(graph).getPath(source, dest).length
    }

    override fun getNeighbors(vertex: String): Set<Topology.Vertex> {
        return Graphs.neighborListOf(this.graph, vertex).map { Topology.Vertex(it) }.toSet()
    }

    override fun isPlanar() = planarityInspector.isPlanar
    override fun isConnected() = connectivityInspector.isConnected

}
