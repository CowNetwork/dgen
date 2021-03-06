package network.cow.dgen.blueprint

import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D

/**
 * @author Tobias Büser
 */
open class NormalRoomBlueprint(
    name: String, outline: Polygon2D,
    passagePoints: List<Vector2D>,
    rotation: Float = 0f
) : RoomBlueprint(name, outline, passagePoints, rotation) {

    override fun rotate(degrees: Float, clockwise: Boolean): RoomBlueprint {
        val rotatedOutline = this.outline.rotate(degrees.toDouble(), clockwise)

        return NormalRoomBlueprint(
            this.name,
            rotatedOutline,
            passagePoints.map { it.rotate(degrees.toDouble(), clockwise) },
            this.rotation + degrees
        )
    }

    override fun shift(by: Vector2D): RoomBlueprint {
        return NormalRoomBlueprint(
            this.name,
            this.outline + by,
            this.passagePoints.map { it + by },
            this.rotation
        )
    }


}
