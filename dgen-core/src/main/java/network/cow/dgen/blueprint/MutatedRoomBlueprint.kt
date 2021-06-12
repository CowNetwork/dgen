package network.cow.dgen.blueprint

import network.cow.dgen.math.Transform
import network.cow.dgen.math.Vector2D

/**
 * @author Tobias Büser
 */
class MutatedRoomBlueprint(
    val original: RoomBlueprint,
    val transforms: List<Transform>,
    val shift: Vector2D
)
