package network.cow.dgen

import network.cow.dgen.blueprint.RoomBlueprint

/**
 * @author Tobias Büser
 */
interface DungeonRoom {

    val id: String
    val depth: Int
    val blueprint: RoomBlueprint
    val doors: Map<Int, String?>

}
