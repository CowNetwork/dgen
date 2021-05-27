package network.cow.dgen

import network.cow.dgen.blueprint.NormalRoomBlueprint

/**
 * @author Tobias Büser
 */
class DungeonNormalRoom(
    override val id: String, override val depth: Int,
    override val blueprint: NormalRoomBlueprint,
    override val passages: MutableMap<Int, String?> = mutableMapOf()
) : DungeonRoom
