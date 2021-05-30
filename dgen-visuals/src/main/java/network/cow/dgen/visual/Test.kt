package network.cow.dgen.visual

import network.cow.dgen.blueprint.FinalRoomBlueprint
import network.cow.dgen.blueprint.NormalRoomBlueprint
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.blueprint.SpawnRoomBlueprint
import network.cow.dgen.generator.SinglePathDungeonGenerator
import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D
import java.io.File
import javax.imageio.ImageIO

/**
 * How does the algorithm work?
 *
 * - We have a current room and the previous room. We want to find every passage of current room, that
 * does not lead to the previous room, because we need to spawn these rooms.
 * - So we want to place the connected rooms to the outerpoint of the passage point they are connecting
 * to. *Unsolved here*: We want to have it easier, so that it knows where to connect without having to
 * calculate the outer point. But that does not work if we only have the relative coordinates of the
 * blueprint of the other connected room.
 *
 * IDEAS:
 * - some sort of object called `joint` where we can get both relative/absolute positions from.
 */
/**
 * @author Tobias Büser
 */
fun main() {
    val blueprints = Blueprints.BLUEPRINTS

    while (true) {
        val hexSeed = "e847fc1a0564"
        val seed = 0L
        println("=== Seed: $hexSeed")
        println()

        val generator = SinglePathDungeonGenerator(seed, blueprints, 77)

        val generatedRooms = generator.generate()
        generatedRooms.forEach {
            println("${it.id}(blueprint: ${it.blueprint.name}, depth: ${it.depth}): " + it.passages.mapValues { pair -> pair.value })
        }
        println()
        println("=== Dungeon successfully generated. ===")

        val visualizer = DungeonVisualizer(*generatedRooms.toTypedArray())
        visualizer.isVisible = true
        break
    }
}

fun getBlueprints(): List<RoomBlueprint> {
    val image = ImageIO.read(File("E:\\2. Projekte\\CowNetwork\\Git\\github.com\\cownetwork\\dgen\\.github\\assets\\rooms_sprite_sheet.png"))
    val rooms = ImageRoomBlueprintLoader(image, 16).load().toMutableList()
    rooms.removeAt(3)

    val polygon = Polygon2D(
        listOf(
            Vector2D(0.0, 0.0),
            Vector2D(4.0, 0.0),
            Vector2D(4.0, 4.0),
            Vector2D(0.0, 4.0),
        )
    )

    rooms.addAll(
        listOf(
            SpawnRoomBlueprint(
                "spawn001",
                polygon,
                listOf(
                    Vector2D(2.0, 0.0)
                ),
                spawnPosition = Vector2D(2.0, 2.0)
            ),
            FinalRoomBlueprint(
                "final001",
                polygon,
                listOf(
                    Vector2D(2.0, 0.0)
                ),
                stairsPosition = Vector2D(2.0, 2.0)
            )
        )
    )
    return rooms
}

class Blueprints {

    companion object {

        val SPAWN001 = SpawnRoomBlueprint(
            "spawn001",
            Polygon2D(
                listOf(
                    Vector2D(0.0, 0.0),
                    Vector2D(0.0, 4.0),
                    Vector2D(4.0, 4.0),
                    Vector2D(4.0, 0.0)
                )
            ),
            listOf(
                Vector2D(2.0, 4.0)
            ),
            spawnPosition = Vector2D(2.0, 2.0)
        )

        val FINAL001 = FinalRoomBlueprint(
            "final001",
            Polygon2D(
                listOf(
                    Vector2D(0.0, 0.0),
                    Vector2D(0.0, 4.0),
                    Vector2D(4.0, 4.0),
                    Vector2D(4.0, 0.0)
                )
            ),
            listOf(
                Vector2D(2.0, 4.0)
            ),
            stairsPosition = Vector2D(2.0, 2.0)
        )

        val NORMAL001 = NormalRoomBlueprint(
            "normal001",
            Polygon2D(
                listOf(
                    Vector2D(0.0, 0.0),
                    Vector2D(8.0, 0.0),
                    Vector2D(8.0, 4.0),
                    Vector2D(5.0, 4.0),
                    Vector2D(5.0, 8.0),
                    Vector2D(0.0, 8.0)
                )
            ),
            listOf(
                Vector2D(2.0, 8.0),
                Vector2D(8.0, 2.0)

            )
        )
        val NORMAL002 = NormalRoomBlueprint(
            "normal002",
            Polygon2D(
                listOf(
                    Vector2D(0.0, 0.0),
                    Vector2D(0.0, 8.0),
                    Vector2D(4.0, 8.0),
                    Vector2D(4.0, 0.0)
                )
            ),
            listOf(
                Vector2D(2.0, 0.0),
                Vector2D(2.0, 8.0)
            )
        )
        val NORMAL003 = NormalRoomBlueprint(
            "normal003",
            Polygon2D(
                listOf(
                    Vector2D(0.0, 0.0),
                    Vector2D(0.0, 4.0),
                    Vector2D(3.0, 4.0),
                    Vector2D(3.0, 7.0),
                    Vector2D(7.0, 7.0),
                    Vector2D(7.0, 4.0),
                    Vector2D(10.0, 4.0),
                    Vector2D(10.0, 0.0)
                )
            ),
            listOf(
                Vector2D(0.0, 2.0),
                Vector2D(10.0, 2.0),
                Vector2D(5.0, 7.0)
            )
        )
        val NORMAL004 = NormalRoomBlueprint(
            "normal004",
            Polygon2D(
                listOf(
                    Vector2D(0.0, 0.0),
                    Vector2D(0.0, 10.0),
                    Vector2D(7.0, 10.0),
                    Vector2D(7.0, 6.0),
                    Vector2D(4.0, 6.0),
                    Vector2D(4.0, 4.0),
                    Vector2D(7.0, 4.0),
                    Vector2D(7.0, 0.0)
                )
            ),
            listOf(
                Vector2D(7.0, 2.0),
                Vector2D(0.0, 5.0),
                Vector2D(7.0, 8.0)
            )
        )

        val BLUEPRINTS = listOf(SPAWN001, FINAL001, NORMAL001, NORMAL002, NORMAL003, NORMAL004)

    }

}
