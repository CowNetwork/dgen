package network.cow.dgen.math.graph

/**
 * This decomposer works by doing a [BreadthFirstSearchIterator] from
 * a starting vertex until the decomposing chunk size is reached.
 *
 * Now if there are still smaller chunks left, we just add them to adjacent chunks,
 * until they reach the chunk size.
 * The search for adjacent chunks always works, because we are working with a
 * connected graph.
 *
 * @author Tobias Büser
 */
class BreadthFirstSearchGraphDecomposer : GraphDecomposer {

    override fun decompose(graph: MutableGraph<*>, chunkMinSize: Int): List<Graph.Chunk> {
        if (!graph.isConnected()) throw IllegalArgumentException("The graph needs to be connected.")

        val chunks = mutableListOf<Graph.Chunk>()

        // if the graph is already less or equal to the chunk size
        // we can just return the existing vertices as a list.
        if (graph.size <= chunkMinSize) {
            return listOf(Graph.Chunk(graph.vertexKeys.toList()))
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
            chunks.add(Graph.Chunk(found))
            graphCopy.removeVertices(*found.toTypedArray())

            current = this.selectNewVertex(graphCopy)
        }

        // now we balance the chains by finding adjacent
        // chains to add them to
        val smallerChunks = chunks.filter { it.size < chunkMinSize }.toMutableList()
        while (smallerChunks.isNotEmpty()) {
            smallerChunks.removeAll { smallerChunk ->
                val adjacentChunks = chunks
                    .filter { it != smallerChunk && it.isNextTo(graph, smallerChunk) }
                    .sortedBy { it.size }
                val adjacent = adjacentChunks.firstOrNull() ?: return@removeAll false

                // merge adjacent with smallerChain
                adjacent.addAll(smallerChunk)
                chunks.remove(smallerChunk)

                adjacent.size >= chunkMinSize
            }
        }
        return chunks
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
