package network.cow.dgen.visual

import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.FinalRoomBlueprint
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.blueprint.SpawnRoomBlueprint
import network.cow.dgen.math.Vector2D
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.WindowConstants


/**
 * Creates a [JFrame] to display a dungeon in.
 * Uses a [JScrollPane] to make it viewable, even if the width
 * and length of the dungeon would exceed that of the computer
 * screen.
 *
 * @author Tobias Büser
 */
class DungeonVisualizer(private vararg val rooms: DungeonRoom) : JFrame() {

    companion object {
        private val SPAWN_ROOM_OUTLINE_COLOR = Color(255, 105, 105)
        private val SPAWN_ROOM_VERTICES_COLOR = Color(209, 84, 84)

        private val NORMAL_ROOM_OUTLINE_COLOR = Color(66, 135, 245)
        private val NORMAL_ROOM_VERTICES_COLOR = Color(56, 114, 207)

        private val FINAL_ROOM_OUTLINE_COLOR = Color(155, 235, 52)
        private val FINAL_ROOM_VERTICES_COLOR = Color(136, 207, 45)

        private val PASSAGE_POINTS_COLOR = Color(62, 250, 250)

        private val BOUNDING_BOX_COLOR = Color(0.62f, 0.62f, 0.62f, 0.1f)
    }

    init {
        this.title = "Dungeon Visualizer"
        this.preferredSize = Dimension(800, 600)
        this.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        val image = createImage()
        val scrollPane = JScrollPane(JLabel(ImageIcon(image)))
        scrollPane.verticalScrollBar.unitIncrement = 32
        scrollPane.horizontalScrollBar.unitIncrement = 32
        scrollPane.preferredSize = this.preferredSize
        this.iconImage = image

        this.add(scrollPane, BorderLayout.CENTER)
        this.pack()
    }

    private fun createImage(): BufferedImage {
        val maxX = rooms.maxOf { it.blueprint.outline.max.x }
        val maxY = rooms.maxOf { it.blueprint.outline.max.y }

        val minX = rooms.minOf { it.blueprint.outline.min.x }
        val minY = rooms.minOf { it.blueprint.outline.min.y }

        val image = BufferedImage((maxX - minX + 11).toInt() * 5, (maxY - minY + 11).toInt() * 5, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.createGraphics()
        this.paintComponent(graphics)

        return image
    }

    private fun paintComponent(graphics: Graphics2D) {
        graphics.background = Color.white
        graphics.translate(10, 15)
        graphics.scale(5.0, 5.0)

        val minX = rooms.minOf { it.blueprint.outline.min.x }
        val minY = rooms.minOf { it.blueprint.outline.min.y }
        val min = Vector2D(minX, minY)
        val distance = Vector2D.ZERO - min

        val translatedRooms: List<RoomBlueprint> = rooms.map { connectedRoom ->
            connectedRoom.blueprint.shift(distance)
        }
        translatedRooms.forEach { this.paintRoomBlueprint(it, graphics) }
    }

    private fun paintRoomBlueprint(blueprint: RoomBlueprint, graphics: Graphics2D) {
        when (blueprint) {
            is SpawnRoomBlueprint -> {
                graphics.color = SPAWN_ROOM_OUTLINE_COLOR
            }
            is FinalRoomBlueprint -> {
                graphics.color = FINAL_ROOM_OUTLINE_COLOR
            }
            else -> {
                graphics.color = NORMAL_ROOM_OUTLINE_COLOR
            }
        }
        blueprint.outline.sides.forEach {
            graphics.drawLine(it.start.x.toInt(), it.start.y.toInt(), it.end.x.toInt(), it.end.y.toInt())
        }

        when (blueprint) {
            is SpawnRoomBlueprint -> {
                graphics.color = SPAWN_ROOM_VERTICES_COLOR
            }
            is FinalRoomBlueprint -> {
                graphics.color = FINAL_ROOM_VERTICES_COLOR
            }
            else -> {
                graphics.color = NORMAL_ROOM_VERTICES_COLOR
            }
        }
        blueprint.outline.vertices.forEach {
            graphics.drawLine(it.x.toInt(), it.y.toInt(), it.x.toInt(), it.y.toInt())
        }

        graphics.paint = BOUNDING_BOX_COLOR
        graphics.drawRect(
            blueprint.outline.min.x.toInt() - 1, blueprint.outline.min.y.toInt() - 1,
            blueprint.outline.length.toInt() + 2, blueprint.outline.width.toInt() + 2
        )

        graphics.color = PASSAGE_POINTS_COLOR
        blueprint.passagePoints.forEach {
            graphics.drawLine(it.x.toInt(), it.y.toInt(), it.x.toInt(), it.y.toInt())
        }
    }

}
