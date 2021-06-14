package network.cow.dgen.blueprint

import network.cow.dgen.math.Transform
import network.cow.dgen.math.Vector2D

/**
 * @author Tobias Büser
 */
class MutatedRoomBlueprint(
    val id: String,
    val blueprint: RoomBlueprint,
    val transforms: List<Transform> = listOf(),
    val shift: Vector2D = Vector2D.ZERO
)
