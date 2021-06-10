package network.cow.dgen.math

import org.jgrapht.Graphs
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph

/**
 * @author Tobias Büser
 */
class JGraphMutableGraph<V> : MutableGraph<V> {

    override val vertices = mutableSetOf<V>()
    private val verticesMap = mutableMapOf<String, V>()

    override val edges = mutableSetOf<Graph.Edge<V>>()

    val jgraph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    private val planarityInspector: BoyerMyrvoldPlanarityInspector<String, DefaultEdge>
    private val connectivityInspector: ConnectivityInspector<String, DefaultEdge>

    init {
        verticesMap.keys.forEach { jgraph.addVertex(it) }
        edges.forEach { jgraph.addEdge(it.sourceKey, it.targetKey) }

        this.planarityInspector = BoyerMyrvoldPlanarityInspector(jgraph)
        this.connectivityInspector = ConnectivityInspector(jgraph)
    }

    override fun getVertex(key: String) = this.verticesMap[key]
    override fun getEdge(sourceKey: String, targetKey: String) = this.edges.find { it.sourceKey == sourceKey && it.targetKey == targetKey }

    override fun getEdgesTo(sourceKey: String) = this.edges.filter { it.sourceKey == sourceKey }
    override fun getEdgesFrom(targetKey: String) = this.edges.filter { it.targetKey == targetKey }

    override fun shortestDistance(sourceKey: String, targetKey: String): Int {
        return DijkstraShortestPath(jgraph).getPath(sourceKey, targetKey).length
    }

    override fun getNeighbors(vertex: String): Set<V> {
        return Graphs.neighborListOf(this.jgraph, vertex).map { verticesMap[it]!! }.toSet()
    }

    override fun isPlanar() = planarityInspector.isPlanar
    override fun isConnected() = connectivityInspector.isConnected

    override fun addVertex(key: String, vertex: V) {
        this.vertices.add(vertex)
        this.verticesMap[key] = vertex

        this.jgraph.addVertex(key)
    }

    override fun addEdge(edge: Graph.Edge<V>) {
        this.edges.add(edge)

        this.jgraph.addEdge(edge.sourceKey, edge.targetKey)
    }

    override fun removeVertex(key: String) {
        this.vertices.remove(this.verticesMap[key]!!)

        this.jgraph.removeVertex(key)
    }

    override fun removeEdge(sourceKey: String, targetKey: String) {
        this.edges.removeIf { it.sourceKey == sourceKey && it.targetKey == targetKey }

        this.jgraph.removeEdge(sourceKey, targetKey)
    }

    override fun removeEdges(filter: (Graph.Edge<V>) -> Boolean) {
        val edges = this.edges.filter(filter)
        this.edges.removeAll(edges)

        edges.forEach { jgraph.removeEdge(it.sourceKey, it.targetKey) }
    }

    override fun deepCopy(): MutableGraph<V> {
        val copy = JGraphMutableGraph<V>()
        this.verticesMap.forEach { (key, vertex) -> copy.addVertex(key, vertex) }
        this.edges.forEach { copy.addEdge(it.sourceKey, it.targetKey) }

        return copy
    }

}
