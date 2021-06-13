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
open class JGraphMutableGraph<V, E>(
    vertices: Map<String, V> = mapOf(),
    edges: Set<Graph.Edge<E>> = setOf()
) : MutableGraph<V, E> {

    private val adjacencyMap = mutableMapOf<String, MutableSet<String>>()
    private val verticesMap = mutableMapOf<String, V>()
    private val edgesMap = mutableMapOf<Pair<String, String>, Graph.Edge<E>>()

    final override val edges: Collection<Graph.Edge<E>>
        get() = edgesMap.values
    final override val vertices: Collection<V>
        get() = verticesMap.values
    final override val vertexKeys: Set<String>
        get() = verticesMap.keys

    val jgraph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    private val planarityInspector: BoyerMyrvoldPlanarityInspector<String, DefaultEdge>
    private val connectivityInspector: ConnectivityInspector<String, DefaultEdge>

    init {
        this.verticesMap.putAll(vertices)
        this.edgesMap.putAll(edges.associateBy { it.from to it.to })

        // fill jgraph with it
        this.verticesMap.keys.forEach { jgraph.addVertex(it) }
        this.edges.forEach { jgraph.addEdge(it.from, it.to) }
        this.vertexKeys.forEach {
            val neighbors = Graphs.neighborListOf(this.jgraph, it).toMutableSet()
            this.adjacencyMap[it] = neighbors
        }

        this.planarityInspector = BoyerMyrvoldPlanarityInspector(jgraph)
        this.connectivityInspector = ConnectivityInspector(jgraph)
    }

    override fun getVertex(key: String) = this.verticesMap[key]

    override fun getEdge(fromTo: Pair<String, String>) = this.edgesMap[fromTo]
    override fun getEdgesTo(from: String) = this.edges.filter { it.from == from }
    override fun getEdgesFrom(to: String) = this.edges.filter { it.to == to }

    override fun shortestDistance(from: String, to: String): Int {
        return DijkstraShortestPath(jgraph).getPath(from, to).length
    }

    override fun getNeighbors(vertex: String) = this.adjacencyMap[vertex] ?: emptySet()

    override fun isPlanar() = planarityInspector.isPlanar
    override fun isConnected() = connectivityInspector.isConnected

    override fun addVertex(key: String, vertex: V) {
        this.verticesMap[key] = vertex

        this.jgraph.addVertex(key)
    }

    override fun addEdge(edge: Graph.Edge<E>) {
        this.edgesMap[edge.from to edge.to] = edge

        this.jgraph.addEdge(edge.from, edge.to)

        // we have a new neighbor pair
        val source = adjacencyMap[edge.from] ?: mutableSetOf()
        source.add(edge.to)
        adjacencyMap[edge.from] = source

        val target = adjacencyMap[edge.to] ?: mutableSetOf()
        target.add(edge.from)
        adjacencyMap[edge.to] = target
    }

    override fun removeVertex(vertex: String) {
        this.verticesMap.remove(vertex) ?: return

        this.jgraph.removeVertex(vertex)

        // remove vertex from all neighbors
        this.adjacencyMap.remove(vertex)
        this.adjacencyMap.values.forEach { it.remove(vertex) }
    }

    override fun removeEdge(from: String, to: String) {
        this.edgesMap.remove(from to to)
        this.edgesMap.remove(to to from)

        this.jgraph.removeEdge(from, to)
        this.jgraph.removeEdge(to, from)

        // they are not neighbors anymore
        this.adjacencyMap[from]?.remove(to)
        this.adjacencyMap[to]?.remove(from)
    }

    override fun removeEdges(filter: (Graph.Edge<E>) -> Boolean) {
        this.edges.filter(filter).forEach { this.removeEdge(it.from, it.to) }
    }

    override fun deepCopy(): MutableGraph<V, E> {
        val copy = JGraphMutableGraph<V, E>()
        this.verticesMap.forEach { (key, vertex) -> copy.addVertex(key, vertex) }
        this.edges.forEach { copy.addEdge(it.from, it.to, it.descriptor) }

        return copy
    }

}
