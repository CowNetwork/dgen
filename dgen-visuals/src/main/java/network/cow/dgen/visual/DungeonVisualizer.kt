package network.cow.dgen.visual

import network.cow.dgen.DungeonFinalRoom
import network.cow.dgen.DungeonNormalRoom
import network.cow.dgen.DungeonRoom
import network.cow.dgen.DungeonSpawnRoom
import network.cow.dgen.blueprint.FinalRoomBlueprint
import network.cow.dgen.blueprint.NormalRoomBlueprint
import network.cow.dgen.blueprint.SpawnRoomBlueprint
import network.cow.dgen.math.Vector2D
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JComponent


/**
 * @author Tobias Büser
 */
class DungeonVisualizer(private vararg val rooms: DungeonRoom) : JComponent() {

    init {
        val maxX = rooms.maxOf { it.blueprint.outline.max.x }
        val maxY = rooms.maxOf { it.blueprint.outline.max.y }

        val minX = rooms.minOf { it.blueprint.outline.min.x }
        val minY = rooms.minOf { it.blueprint.outline.min.y }

        this.preferredSize = Dimension(600, 600)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val graphics = g as Graphics2D

        g.background = Color.white
        /*graphics.color = Color.white
        graphics.fillRect(0, 0, width, height)*/

        graphics.translate(10, 10)
        graphics.scale(5.0, 5.0)

        val minX = rooms.minOf { it.blueprint.outline.min.x }
        val minY = rooms.minOf { it.blueprint.outline.min.y }
        val min = Vector2D(minX, minY)

        val translatedRooms: List<DungeonRoom> = rooms.map { connectedRoom ->
            when (val blueprint = connectedRoom.blueprint) {
                is SpawnRoomBlueprint -> {
                    DungeonSpawnRoom(
                        connectedRoom.id,
                        connectedRoom.depth,
                        SpawnRoomBlueprint(
                            connectedRoom.blueprint.name,
                            blueprint.outline + (Vector2D.NULL - min),
                            blueprint.passagePoints.map { it + (Vector2D.NULL - min) },
                            spawnPosition = blueprint.spawnPosition + (Vector2D.NULL - min)
                        )
                    )
                }
                is FinalRoomBlueprint -> {
                    DungeonFinalRoom(
                        connectedRoom.id,
                        connectedRoom.depth,
                        FinalRoomBlueprint(
                            blueprint.name,
                            blueprint.outline + (Vector2D.NULL - min),
                            blueprint.passagePoints.map { it + (Vector2D.NULL - min) },
                            stairsPosition = blueprint.stairsPosition + (Vector2D.NULL - min)
                        ),
                        connectedRoom.passages
                    )
                }
                else -> {
                    DungeonNormalRoom(
                        connectedRoom.id,
                        connectedRoom.depth,
                        NormalRoomBlueprint(
                            blueprint.name,
                            blueprint.outline + (Vector2D.NULL - min),
                            blueprint.passagePoints.map { it + (Vector2D.NULL - min) }
                        ),
                        connectedRoom.passages
                    )
                }
            }
        }
        translatedRooms.forEach { this.paintRoom(it, graphics) }
    }

    private fun paintRoom(room: DungeonRoom, graphics: Graphics2D) {
        val blueprint = room.blueprint

        when (blueprint) {
            is SpawnRoomBlueprint -> {
                graphics.color = Color(255, 105, 105)
            }
            is FinalRoomBlueprint -> {
                graphics.color = Color(155, 235, 52)
            }
            else -> {
                graphics.color = Color(66, 135, 245)
            }
        }
        blueprint.outline.sides.forEach {
            graphics.drawLine(it.start.x.toInt(), it.start.y.toInt(), it.end.x.toInt(), it.end.y.toInt())
        }

        when (blueprint) {
            is SpawnRoomBlueprint -> {
                graphics.color = Color(209, 84, 84)
            }
            is FinalRoomBlueprint -> {
                graphics.color = Color(136, 207, 45)
            }
            else -> {
                graphics.color = Color(56, 114, 207)
            }
        }
        blueprint.outline.vertices.forEach {
            graphics.drawLine(it.x.toInt(), it.y.toInt(), it.x.toInt(), it.y.toInt())
        }

        graphics.paint = Color(0.62f, 0.62f, 0.62f, 0.1f)
        graphics.drawRect(
            blueprint.outline.min.x.toInt() - 1, blueprint.outline.min.y.toInt() - 1,
            blueprint.outline.length.toInt() + 2, blueprint.outline.width.toInt() + 2
        )

        graphics.color = Color(62, 250, 250)
        blueprint.passagePoints.forEach {
            graphics.drawLine(it.x.toInt(), it.y.toInt(), it.x.toInt(), it.y.toInt())
        }
    }

}
