package network.cow.dgen.visual

import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.blueprint.RoomBlueprintLoader
import network.cow.dgen.math.Line2D
import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Loads [NormalRoomBlueprint]s from a [BufferedImage], which then cuts the image in
 * [gridSize] big chunks and converts it into a blueprint.
 *
 * @author Tobias Büser
 */
class ImageRoomBlueprintLoader(private val image: BufferedImage, private val gridSize: Int) : RoomBlueprintLoader {

    companion object {
        private val VERTICE_COLOR = Color(56, 114, 207)
        private val PASSAGE_POINT_COLOR = Color(62, 250, 250)
        private val POINT_COLOR = Color(66, 135, 245)
    }

    override fun load(): List<RoomBlueprint> {
        if (image.height > gridSize && image.width > gridSize) {
            val blueprints = mutableListOf<RoomBlueprint>()

            for (y in 0 until image.height step gridSize) {
                for (x in 0 until image.width step gridSize) {
                    val subImage = image.getSubimage(x, y, gridSize, gridSize)
                    blueprints.addAll(ImageRoomBlueprintLoader(subImage, gridSize).load())
                }
            }
            return blueprints
        }

        val blueprints = mutableListOf<RoomBlueprint>()

        val vertices = mutableListOf<Vector2D>()
        val passagePoints = mutableListOf<Vector2D>()
        val otherPoints = mutableListOf<Vector2D>()

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                when (getColorOfPixel(image, x, y)) {
                    VERTICE_COLOR -> {
                        vertices.add(Vector2D(x.toDouble(), y.toDouble()))
                    }
                    PASSAGE_POINT_COLOR -> {
                        passagePoints.add(Vector2D(x.toDouble(), y.toDouble()))
                    }
                    POINT_COLOR -> {
                        otherPoints.add(Vector2D(x.toDouble(), y.toDouble()))
                    }
                }
            }
        }

        val sortedVertices = sortVertices(vertices, otherPoints + passagePoints)
        if (sortedVertices.isEmpty()) return emptyList()

        blueprints.add(RoomBlueprint("${vertices.size}v${passagePoints.size}p${otherPoints.size}o", Polygon2D(sortedVertices), passagePoints))

        return blueprints
    }

    private fun sortVertices(vertices: List<Vector2D>, otherPoints: List<Vector2D>): List<Vector2D> {
        if (vertices.isEmpty()) return vertices
        val copy = vertices.toMutableList()
        val sortedVertices = mutableListOf<Vector2D>()

        var current = copy.minByOrNull { (Vector2D.ZERO - it).magnitude }!!
        while (sortedVertices.size < vertices.size) {
            sortedVertices.add(current)
            copy.remove(current)

            val new = copy.sortedBy { (current - it).magnitude }.firstOrNull {
                // check if otherPoints are in between
                val line = Line2D(current, it)
                if (!otherPoints.any { p -> p in line }) {
                    return@firstOrNull false
                }

                (it.x == current.x && it.y != current.y)
                    || (it.y == current.y && it.x != current.x)
            } ?: break
            current = new
        }

        return sortedVertices
    }

    private fun getColorOfPixel(image: BufferedImage, x: Int, y: Int): Color {
        val c = image.getRGB(x, y)
        val red = c and 0x00ff0000 shr 16
        val green = c and 0x0000ff00 shr 8
        val blue = c and 0x000000ff
        return Color(red, green, blue)
    }

}
