package network.cow.dgen.room

import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.RoomBlueprint

/**
 * @author Tobias Büser
 */
class DungeonEmptyRoom(
    override val id: String, override val depth: Int,
    override val blueprint: RoomBlueprint, override val doors: Map<Int, String?> = mutableMapOf()
) : DungeonRoom {
}
