package network.cow.dgen

import network.cow.dgen.blueprint.SpawnRoomBlueprint

/**
 * @author Tobias Büser
 */
class DungeonSpawnRoom(
    override val id: String, override val depth: Int,
    override val blueprint: SpawnRoomBlueprint,
    override val passages: MutableMap<Int, String?> = mutableMapOf()
) : DungeonRoom
