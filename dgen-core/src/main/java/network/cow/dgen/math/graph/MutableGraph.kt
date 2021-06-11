package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
interface MutableGraph<V> : Graph<V> {

    fun addVertex(key: String, vertex: V)
    fun addEdge(edge: Graph.Edge)
    fun addEdge(sourceKey: String, targetKey: String) = this.addEdge(Graph.Edge(sourceKey, targetKey))

    fun removeVertex(key: String)
    fun removeVertices(vararg keys: String) = keys.forEach { removeVertex(it) }

    fun removeEdge(sourceKey: String, targetKey: String)
    fun removeEdges(filter: (Graph.Edge) -> Boolean)

    fun removeEdgesFrom(sourceKey: String) = this.removeEdges { it.sourceKey == sourceKey }
    fun removeEdgesTo(targetKey: String) = this.removeEdges { it.targetKey == targetKey }

    fun deepCopy(): MutableGraph<V>

}
