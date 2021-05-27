package network.cow.dgen

import network.cow.dgen.blueprint.FinalRoomBlueprint

/**
 * @author Tobias Büser
 */
class DungeonFinalRoom(
    override val id: String, override val depth: Int,
    override val blueprint: FinalRoomBlueprint,
    override val passages: MutableMap<Int, String?> = mutableMapOf()
) : DungeonRoom
