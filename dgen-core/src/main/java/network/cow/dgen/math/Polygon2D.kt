package network.cow.dgen.math

/**
 * @author Tobias Büser
 */
class Polygon2D(val vertices: List<Vector2D>) {

    val min = Vector2D(vertices.minOf { it.x }, vertices.minOf { it.y })
    val max = Vector2D(vertices.maxOf { it.x }, vertices.maxOf { it.y })
    val length = max.x - min.x
    val width = max.y - min.y

    val edges: List<Line2D>
    val orientation: Orientation

    init {
        val edges = mutableListOf<Line2D>()

        var previousVertex: Vector2D? = null
        for (vertex in vertices) {
            if (previousVertex != null) {
                edges.add(Line2D(previousVertex, vertex))
            }
            previousVertex = vertex
        }
        edges.add(Line2D(previousVertex!!, vertices.first()))
        this.edges = edges

        this.orientation = this.calculateOrientation()
    }

    fun rotate(degrees: Double): Polygon2D {
        val rotatedVertices = vertices.toMutableList()
        vertices.forEachIndexed { index, vector ->
            rotatedVertices[index] = vector.rotate(degrees)
        }
        return Polygon2D(rotatedVertices)
    }

    fun overlapsWith(other: Polygon2D): Boolean {
        return this.edges.any { side -> other.edges.any { it.intersectsWith(side) } }
    }

    /**
     * Reverses the [Orientation] by fixing the first vertex and
     * reversing the other vertices.
     */
    fun reverseOrientation(): Polygon2D {
        return Polygon2D(listOf(vertices.first()) + vertices.drop(1).reversed())
    }

    operator fun plus(other: Vector2D): Polygon2D {
        val shiftedVertices = vertices.toMutableList()
        vertices.forEachIndexed { index, vector ->
            shiftedVertices[index] = vector + other
        }
        return Polygon2D(shiftedVertices)
    }

    operator fun plus(value: Double) = this.plus(Vector2D(value, value))

    operator fun contains(point: Vector2D): Boolean {
        if (point.x !in min.x..max.x || point.y !in min.y..max.y) {
            // if it's not inside the bounding box
            return false
        }
        if (edges.any { point in it }) {
            // if it's on the outline
            return true
        }

        // choose a point outside the polygon
        val outside = Vector2D(min.x - 10, point.y)
        val ray = Line2D(point, outside)

        var intersections = 0
        edges.forEach {
            if (ray.intersectsWith(it)) {
                intersections++
            }
        }
        return intersections % 2 != 0
    }

    /**
     * Calculates the orientation of the vertices in this polygon.
     * Uses the sign of the enclosed area as the deciding factor.
     */
    private fun calculateOrientation(): Orientation {
        val sum = this.edges.sumOf {
            val (x1, y1) = it.start
            val (x2, y2) = it.end

            (x2 - x1) * (y2 + y1)
        }

        return when {
            sum >= 0 -> Orientation.CLOCKWISE
            sum < 0 -> Orientation.COUNTER_CLOCKWISE
            else -> Orientation.NOT_DETERMINABLE
        }
    }

    override fun toString(): String {
        return "Polygon2D($vertices)"
    }

}
