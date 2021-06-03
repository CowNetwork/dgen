package network.cow.dgen.room

import network.cow.dgen.DungeonRoom

/**
 * @author Tobias Büser
 */
class DungeonFinalRoom(
    override val id: String, override val depth: Int,
    override val blueprint: FinalRoomBlueprint, override val doors: Map<Int, String?> = mutableMapOf()
) : DungeonRoom {
}
