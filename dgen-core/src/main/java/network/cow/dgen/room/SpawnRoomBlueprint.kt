package network.cow.dgen.room

import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D

/**
 * @author Tobias Büser
 */
class SpawnRoomBlueprint(
    name: String, outline: Polygon2D,
    doors: List<Vector2D>, rotation: Float = 0f
) : RoomBlueprint(name, outline, doors, rotation)
