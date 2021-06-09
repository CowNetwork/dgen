package network.cow.dgen.topology

/**
 * @author Tobias Büser
 */
class BreadthSearchTopologyDecomposer : TopologyDecomposer {

    override fun decompose(topology: Topology): List<Topology.Chain> {
        val chains = mutableListOf<Topology.Chain>()

        while (true) {
            // choose current, which is not part of any face yet
            val current = topology.vertices.firstOrNull { it !in chains.flatten() } ?: break

            val face = this.getFace(topology, current, chains)

            chains.add(Topology.Chain(face))
        }
        return chains
    }

    private fun getFace(
        topology: Topology, start: Topology.Vertex,
        faces: List<Topology.Chain>
    ): List<Topology.Vertex> {
        //val embedding = topology.embedding

        val face = mutableListOf<Topology.Vertex>()
        /*face.add(start)

        var current = start
        while (true) {
            val neighbors = embedding.getNeighborsAround(current)
            if (neighbors.isEmpty()) break

            val next = neighbors.firstOrNull() ?: break
            if (next in face) break

            face.add(next)

            if (next == start) break
            current = next
        }*/
        return face
    }

}
