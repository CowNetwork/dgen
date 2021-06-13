package network.cow.dgen.math.graph

/**
 * A partition is just a collection of vertices from a [Graph], that are
 * connected to each other. The order is important when trying to
 * construct a graph out of the partition again.
 * But the order of the list does not correspond to the real connection between them!
 */
class OrderedPartition(vertices: List<String>) : ArrayList<String>(vertices) {

    /**
     * Two partitions are next to each other, when at least
     * one vertex of this partition has a common edge in [graph]
     * with one vertex of the [other] partition.
     */
    fun isNextTo(graph: Graph<*, *>, other: OrderedPartition): Boolean {
        for (vertex in this) {
            val neighbors = graph.getNeighbors(vertex)
            for (otherVertex in other) {
                if (otherVertex in neighbors) return true
            }
        }
        return false
    }

}
