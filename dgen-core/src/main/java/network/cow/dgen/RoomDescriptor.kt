package network.cow.dgen

import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.math.Transform

/**
 * TODO
 * information during the generation
 *
 * @author Tobias Büser
 */
class RoomDescriptor(
    val blueprint: RoomBlueprint,
    val transforms: List<Transform>,
    val possibleFits: List<RoomBlueprint>,
    val connectedDoors: Map<String, String>,
)
