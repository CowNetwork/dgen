package network.cow.dgen

import network.cow.dgen.blueprint.RoomBlueprint

/**
 * @author Tobias Büser
 */
interface DungeonRoom {

    val id: String
    val depth: Int
    val blueprint: RoomBlueprint

    // TODO should be a non mutable map
    val passages: MutableMap<Int, String?>

}
