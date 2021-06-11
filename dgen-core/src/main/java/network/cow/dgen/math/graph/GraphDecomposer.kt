package network.cow.dgen.math.graph

/**
 * A decomposition algorithm splits the graph in (seemingly)
 * even parts. Normally, doing that correctly would be an NP-complete problem,
 * but we don't want to go overkill.
 * As such, this decomposer just needs to make sure, that the graph is splitted
 * into chunks and that each chunk is at least a specific size large.
 *
 * Meaning that there are no other hard conditions! Otherwise, as mentioned, we wouldn't
 * be able to develop an efficient algorithm in polynomial time.
 *
 * @author Tobias Büser
 */
interface GraphDecomposer {

    companion object {
        private const val AVERAGE_CHUNK_MIN_SIZE = 4
    }

    fun decompose(graph: MutableGraph<*>, chunkMinSize: Int = AVERAGE_CHUNK_MIN_SIZE): List<Graph.Chunk>

}
