package network.cow.dgen.topology

/**
 * @author Tobias Büser
 */
class JGraphBreadthSearchDecomposer : Decomposer<JGraphTopology> {

    private val AVERAGE_MIN_SIZE = 4

    /**
     * TODO
     * 1. Get a vertex with few connections
     * 2. Breadth Search until a minimum size is reached
     * 3. Remove these vertices from the graph
     * 4. Repeat 1. to 3.
     * 5. Balance out, so that every part has a similar size
     */
    override fun decompose(topology: JGraphTopology): List<Topology.Chain> {
        val chains = mutableListOf<Topology.Chain>()



        while (true) {
            break

            // choose current, which is not part of any face yet
            /*val current = topology.vertices.firstOrNull { it !in chains.flatten() } ?: break

            val face = this.getFace(topology, current, chains)

            chains.add(Topology.Chain(face))*/
        }
        return chains
    }

    private fun d(topology: JGraphTopology): List<List<String>> {
        val chains = mutableListOf<List<String>>()
        val graphClone = topology.graph.copy()

        // if there are no vertices, we can't build chains
        if (graphClone.vertexSet().isEmpty()) return emptyList()

        // check if size is less than eq the avg min size
        // cause then we can just return the existing vertices
        if (graphClone.vertexSet().size <= AVERAGE_MIN_SIZE) {
            return listOf(graphClone.vertexSet().toList())
        }

        // minimum size of each chain
        // - if we then have a 4-chain and a 3-chain, we balance it to a 7-chain
        // - if we have a 4-chain and a 4-chain, we dont balance.
        // - if we have 4, 4 and 3, we balance it out, so that either we have 4-7 or
        // if the chain is next to both, we can split it and pass the vertices to both chains
        // to get something like 5-6 or 6-5 respectively.
        val minSize = minOf(graphClone.vertexSet().size, AVERAGE_MIN_SIZE)

        // select the first vertex to start
        var current = graphClone.vertexSet().random()

        return chains
    }

}
