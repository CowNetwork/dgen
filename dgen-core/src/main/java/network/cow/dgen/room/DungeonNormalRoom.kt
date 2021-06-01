package network.cow.dgen.room

import network.cow.dgen.DungeonRoom

/**
 * @author Tobias Büser
 */
class DungeonNormalRoom(
    override val id: String, override val depth: Int,
    override val blueprint: NormalRoomBlueprint,
    override val doors: MutableMap<Int, String?> = mutableMapOf()
) : DungeonRoom
