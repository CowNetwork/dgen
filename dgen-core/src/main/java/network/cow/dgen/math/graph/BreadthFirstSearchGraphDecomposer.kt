package network.cow.dgen.math.graph

/**
 * This decomposer works by doing a [BreadthFirstSearchIterator] from
 * a starting vertex until the decomposing partition size is reached.
 *
 * Now if there are still smaller partitions left, we just add them to adjacent partitions,
 * until they reach the partition size.
 * The search for adjacent partitions always works, because we are working with a
 * connected graph.
 *
 * @author Tobias Büser
 */
class BreadthFirstSearchGraphDecomposer : GraphDecomposer {

    override fun decompose(graph: MutableGraph<*>, partitionMinSize: Int): List<Graph.Partition> {
        if (!graph.isConnected()) throw IllegalArgumentException("The graph needs to be connected.")

        val partitions = mutableListOf<Graph.Partition>()

        // if the graph is already less or equal to the partition size
        // we can just return the existing vertices as a list.
        if (graph.size <= partitionMinSize) {
            return listOf(Graph.Partition(graph.vertexKeys.toList()))
        }

        val graphCopy = graph.deepCopy()

        // select the first vertex to start
        var current: String? = this.selectNewVertex(graphCopy)

        while (current != null) {
            val search = BreadthFirstSearchIterator(graphCopy, current)
            val found = mutableListOf<String>()

            while (search.hasNext() && found.size < partitionMinSize) {
                found.add(search.next())
            }

            // we have one chain, remove it from graph and set new random vertex
            partitions.add(Graph.Partition(found))
            graphCopy.removeVertices(*found.toTypedArray())

            current = this.selectNewVertex(graphCopy)
        }

        // now we balance the chains by finding adjacent
        // chains to add them to
        val smallerParts = partitions.filter { it.size < partitionMinSize }.toMutableList()
        while (smallerParts.isNotEmpty()) {
            smallerParts.removeAll { smallerChunk ->
                val adjacentParts = partitions
                    .filter { it != smallerChunk && it.isNextTo(graph, smallerChunk) }
                    .sortedBy { it.size }
                val adjacent = adjacentParts.firstOrNull() ?: return@removeAll false

                // merge adjacent with smallerChain
                adjacent.addAll(smallerChunk)
                partitions.remove(smallerChunk)

                adjacent.size >= partitionMinSize
            }
        }
        return partitions
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
