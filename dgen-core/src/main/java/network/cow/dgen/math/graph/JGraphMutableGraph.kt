package network.cow.dgen.math.graph

import org.jgrapht.Graphs
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph

/**
 * @author Tobias Büser
 */
open class JGraphMutableGraph<V>(
    vertices: Map<String, V> = mapOf(),
    edges: Set<Graph.Edge> = setOf()
) : MutableGraph<V> {

    private val adjacencyMap = mutableMapOf<String, MutableSet<String>>()
    private val verticesMap = mutableMapOf<String, V>()

    final override val edges = mutableSetOf<Graph.Edge>()

    final override val vertices: Collection<V>
        get() = verticesMap.values
    final override val vertexKeys: Set<String>
        get() = verticesMap.keys

    val jgraph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    private val planarityInspector: BoyerMyrvoldPlanarityInspector<String, DefaultEdge>
    private val connectivityInspector: ConnectivityInspector<String, DefaultEdge>

    init {
        this.verticesMap.putAll(vertices)
        this.edges.addAll(edges)

        // fill jgraph with it
        this.verticesMap.keys.forEach { jgraph.addVertex(it) }
        this.edges.forEach { jgraph.addEdge(it.sourceKey, it.targetKey) }
        this.vertexKeys.forEach {
            val neighbors = Graphs.neighborListOf(this.jgraph, it).toMutableSet()
            this.adjacencyMap[it] = neighbors
        }

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

    override fun getNeighbors(vertexKey: String) = this.adjacencyMap[vertexKey] ?: emptySet()

    override fun isPlanar() = planarityInspector.isPlanar
    override fun isConnected() = connectivityInspector.isConnected

    override fun addVertex(key: String, vertex: V) {
        this.verticesMap[key] = vertex

        this.jgraph.addVertex(key)
    }

    override fun addEdge(edge: Graph.Edge) {
        this.edges.add(edge)

        this.jgraph.addEdge(edge.sourceKey, edge.targetKey)

        // we have a new neighbor pair
        val source = adjacencyMap[edge.sourceKey] ?: mutableSetOf()
        source.add(edge.targetKey)
        adjacencyMap[edge.sourceKey] = source

        val target = adjacencyMap[edge.targetKey] ?: mutableSetOf()
        target.add(edge.sourceKey)
        adjacencyMap[edge.targetKey] = target
    }

    override fun removeVertex(key: String) {
        this.verticesMap.remove(key) ?: return

        this.jgraph.removeVertex(key)

        // remove vertex from all neighbors
        this.adjacencyMap.remove(key)
        this.adjacencyMap.values.forEach { it.remove(key) }
    }

    override fun removeEdge(sourceKey: String, targetKey: String) {
        this.edges.removeIf { it.sourceKey == sourceKey && it.targetKey == targetKey }

        this.jgraph.removeEdge(sourceKey, targetKey)

        // they are not neighbors anymore
        this.adjacencyMap[sourceKey]?.remove(targetKey)
        this.adjacencyMap[targetKey]?.remove(sourceKey)
    }

    override fun removeEdges(filter: (Graph.Edge) -> Boolean) {
        this.edges.filter(filter).forEach { this.removeEdge(it.sourceKey, it.targetKey) }
    }

    override fun deepCopy(): MutableGraph<V> {
        val copy = JGraphMutableGraph<V>()
        this.verticesMap.forEach { (key, vertex) -> copy.addVertex(key, vertex) }
        this.edges.forEach { copy.addEdge(it.sourceKey, it.targetKey) }

        return copy
    }

}
