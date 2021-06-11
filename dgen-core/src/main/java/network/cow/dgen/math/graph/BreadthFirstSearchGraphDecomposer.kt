package network.cow.dgen.math.graph

/**
 * This decomposer works by doing a [BreadthFirstSearchIterator] from
 * a starting vertex until the decomposing chunk size is reached.
 *
 * Now if there are still smaller chains left, we just add them to adjacent chains,
 * until they reach the chunk size.
 * The search for adjacent chains always works, because we are working with a
 * connected graph.
 *
 * @author Tobias Büser
 */
class BreadthFirstSearchGraphDecomposer : GraphDecomposer {

    override fun decompose(graph: MutableGraph<*>, chunkMinSize: Int): List<Graph.DirectedChain> {
        if (!graph.isConnected()) throw IllegalArgumentException("The graph needs to be connected.")

        val chains = mutableListOf<Graph.DirectedChain>()

        // if the graph is already less or equal to the chunk size
        // we can just return the existing vertices as a list.
        if (graph.size <= chunkMinSize) {
            return listOf(Graph.DirectedChain(graph.vertexKeys.toList()))
        }

        val graphCopy = graph.deepCopy()

        // select the first vertex to start
        var current: String? = this.selectNewVertex(graphCopy)

        while (current != null) {
            val search = BreadthFirstSearchIterator(graphCopy, current)
            val found = mutableListOf<String>()

            while (search.hasNext() && found.size < chunkMinSize) {
                found.add(search.next())
            }

            // we have one chain, remove it from graph and set new random vertex
            chains.add(Graph.DirectedChain(found))
            graphCopy.removeVertices(*found.toTypedArray())

            current = this.selectNewVertex(graphCopy)
        }

        // now we balance the chains by finding adjacent
        // chains to add them to
        val smallerChains = chains.filter { it.size < chunkMinSize }.toMutableList()
        while (smallerChains.isNotEmpty()) {
            smallerChains.removeAll { smallerChain ->
                val adjacentChains = chains
                    .filter { it != smallerChain && it.isNextTo(graph, smallerChain) }
                    .sortedBy { it.size }
                val adjacent = adjacentChains.firstOrNull() ?: return@removeAll false

                // merge adjacent with smallerChain
                adjacent.addAll(smallerChain)
                chains.remove(smallerChain)

                adjacent.size >= chunkMinSize
            }
        }
        return chains
    }

    /**
     * When selecting a new vertex, we just check for the vertex with the
     * fewest neighbours, as surely by removing this vertex, we won't leave
     * big gaps in the middle of the [graph].
     *
     * This makes sure that the [graph] stays as balanced, as possible.
     *
     * If no vertex is left, it won't find one, returning null.
     */
    private fun selectNewVertex(graph: Graph<*>): String? {
        return graph.vertexKeys.minByOrNull { graph.getNeighbors(it).size }
    }

}
