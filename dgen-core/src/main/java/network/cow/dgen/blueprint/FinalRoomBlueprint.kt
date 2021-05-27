package network.cow.dgen.blueprint

import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D

/**
 * @author Tobias Büser
 */
class FinalRoomBlueprint(
    name: String, outline: Polygon2D,
    val passagePoint: Vector2D, rotation: Float = 0f,
    val stairsPosition: Vector2D
) : RoomBlueprint(name, outline, listOf(passagePoint), rotation) {

    init {
        if (stairsPosition !in outline) throw IllegalArgumentException("The stairsPosition needs to be in the outline.")
    }

    override fun rotate(degrees: Float, clockwise: Boolean): RoomBlueprint {
        val rotatedOutline = this.outline.rotate(degrees.toDouble(), clockwise)

        return FinalRoomBlueprint(
            this.name,
            rotatedOutline,
            passagePoint.rotate(degrees.toDouble(), clockwise),
            this.rotation + degrees,
            stairsPosition.rotate(degrees.toDouble(), clockwise)
        )
    }

    override fun shift(by: Vector2D): RoomBlueprint {
        return FinalRoomBlueprint(
            this.name,
            this.outline + by,
            this.passagePoint + by,
            this.rotation,
            this.stairsPosition + by
        )
    }

}
