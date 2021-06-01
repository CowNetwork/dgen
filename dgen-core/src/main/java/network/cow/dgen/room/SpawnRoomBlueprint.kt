package network.cow.dgen.room

import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D

/**
 * @author Tobias Büser
 */
class SpawnRoomBlueprint(
    name: String, outline: Polygon2D,
    passagePoints: List<Vector2D>, rotation: Float = 0f,
    val spawnPosition: Vector2D
) : RoomBlueprint(name, outline, passagePoints, rotation) {

    init {
        if (spawnPosition !in outline) throw IllegalArgumentException("The spawnPosition needs to be in the outline.")
    }

    override fun rotate(degrees: Float, clockwise: Boolean): RoomBlueprint {
        val rotatedOutline = this.outline.rotate(degrees.toDouble(), clockwise)

        return SpawnRoomBlueprint(
            this.name,
            rotatedOutline,
            doors.map { it.rotate(degrees.toDouble(), clockwise) },
            this.rotation + degrees,
            spawnPosition.rotate(degrees.toDouble(), clockwise)
        )
    }

    override fun shift(by: Vector2D): RoomBlueprint {
        return SpawnRoomBlueprint(
            this.name,
            this.outline + by,
            this.doors.map { it + by },
            this.rotation,
            this.spawnPosition + by
        )
    }

}
