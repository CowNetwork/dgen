package network.cow.dgen.spigot

import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D
import network.cow.dgen.room.FinalRoomBlueprint
import network.cow.dgen.room.NormalRoomBlueprint
import network.cow.dgen.room.SpawnRoomBlueprint

/**
 * @author Tobias Büser
 */
class Cockbitch {

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
