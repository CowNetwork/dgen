package network.cow.dgen.math.graph

/**
 * @author Tobias Büser
 */
class BreadthFirstSearchGraphDecomposer : GraphDecomposer {

    companion object {
        private const val AVERAGE_MIN_SIZE = 4
    }

    override fun decompose(graph: MutableGraph<*>): List<Graph.DirectedChain<String>> {
        val chains = mutableListOf<Graph.DirectedChain<String>>()

        // if the graph is already less or equal to the avg size
        // we can just turn the vertices set to a list.
        if (graph.size <= AVERAGE_MIN_SIZE) {
            return listOf(Graph.DirectedChain(graph.vertexKeys.toList()))
        }

        val graphCopy = graph.deepCopy()

        // select the first vertex to start
        var current: String? = this.selectNewVertex(graphCopy)

        while (current != null) {
            val search = BreadthFirstSearchIterator(graphCopy, current)
            val found = mutableListOf<String>()

            while (search.hasNext() && found.size < AVERAGE_MIN_SIZE) {
                found.add(search.next())
            }

            // we have one chain, remove it from graph and set new random vertex
            chains.add(Graph.DirectedChain(found))
            graphCopy.removeVertices(*found.toTypedArray())

            current = this.selectNewVertex(graphCopy)
        }

        return chains
    }

    private fun selectNewVertex(graph: Graph<*>): String? {
        return graph.vertexKeys.randomOrNull()
    }

}
