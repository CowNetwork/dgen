package network.cow.dgen.math

/**
 * @author Tobias Büser
 */
class Line2D(val start: Vector2D, val end: Vector2D) {

    private val max = Vector2D(maxOf(start.x, end.x), maxOf(start.y, end.y))
    private val min = Vector2D(minOf(start.x, end.x), minOf(start.y, end.y))

    val isVertical = (start.x - end.x) == 0.0
    val isHorizontal = (start.y - end.y) == 0.0

    fun intersectsWith(other: Line2D): Boolean {
        return java.awt.geom.Line2D.linesIntersect(
            this.start.x, this.start.y, this.end.x, this.end.y,
            other.start.x, other.start.y, other.end.x, other.end.y
        )
    }

    operator fun contains(point: Vector2D): Boolean {
        return (point.x in min.x..max.x) && (point.y in min.y..max.y)
    }

    override fun toString(): String {
        return "Line2D(start=$start, end=$end)"
    }

}
