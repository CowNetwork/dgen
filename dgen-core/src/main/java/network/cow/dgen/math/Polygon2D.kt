package network.cow.dgen.math

/**
 * @author Tobias Büser
 */
open class Polygon2D(val vertices: List<Vector2D>) {

    val min = Vector2D(vertices.minOf { it.x }, vertices.minOf { it.y })
    val max = Vector2D(vertices.maxOf { it.x }, vertices.maxOf { it.y })
    val length = max.x - min.x
    val width = max.y - min.y

    val sides: List<Line2D>

    init {
        val sides = mutableListOf<Line2D>()

        var previousVertex: Vector2D? = null
        for (vertex in vertices) {
            if (previousVertex != null) {
                sides.add(Line2D(previousVertex, vertex))
            }
            previousVertex = vertex
        }
        sides.add(Line2D(previousVertex!!, vertices.first()))

        this.sides = sides
    }

    fun rotate(degrees: Double, clockwise: Boolean = true): Polygon2D {
        val rotatedVertices = vertices.toMutableList()
        vertices.forEachIndexed { index, vector ->
            rotatedVertices[index] = vector.rotate(degrees, clockwise)
        }
        return Polygon2D(rotatedVertices)
    }

    fun overlapsWith(other: Polygon2D): Boolean {
        return this.sides.any { side -> other.sides.any { it.intersectsWith(side) } }
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
            // if it's not inside the bounding box of this polygon
            return false
        }
        if (sides.any { point in it }) {
            // if it's on the outline
            return true
        }

        // choose a point outside the polygon
        val outside = Vector2D(min.x - 10, point.y)
        val ray = Line2D(point, outside)

        var intersections = 0
        sides.forEach {
            if (ray.intersectsWith(it)) {
                intersections++
            }
        }
        return intersections % 2 != 0
    }

    override fun toString(): String {
        return "Polygon2D(vertices=$vertices, min=$min, max=$max, length=$length, width=$width)"
    }


}
