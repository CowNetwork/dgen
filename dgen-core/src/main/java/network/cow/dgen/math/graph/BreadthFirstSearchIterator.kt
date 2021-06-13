package network.cow.dgen.math.graph

import java.util.LinkedList
import java.util.Queue

/**
 * Implements the breadth-first search (BFS) algorithm, which is an
 * algorithm to traverse a graph structure.
 *
 * It works by starting at a vertex and exploring the neighbor vertices and
 * doing that repeatedly, until every vertex of the graph has been visited.
 *
 * In our case we have a queue, where every vertex we yet have to visit is enqueued.
 * If a vertex has been enqueued, we also mark it as visited, as in graphs we could
 * encounter a cycle, which would result in visiting a vertex twice.
 *
 * **Time complexity**:
 * Let V be the number of vertices and E the number of edges.
 * Then the complexity is given as **O(V + E)**
 *
 * @author Tobias Büser
 */
class BreadthFirstSearchIterator(val graph: Graph<*, *>, start: String) : Iterator<String> {

    private val visited = mutableMapOf<String, Boolean>()
    private val queue: Queue<String> = LinkedList()

    init {
        visited[start] = true
        queue.offer(start)
    }

    override fun hasNext() = queue.isNotEmpty()

    override fun next(): String {
        val next = queue.poll()

        val neighbors = graph.getNeighbors(next).filterNot { visited[it] ?: false }
        neighbors.forEach {
            visited[it] = true
            queue.offer(it)
        }

        return next
    }

}
