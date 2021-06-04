package network.cow.dgen.generator

import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.RoomBlueprint
import kotlin.random.Random

/**
 * @author Tobias Büser
 */
abstract class DungeonGenerator(
    val seed: Long,
    val blueprints: List<RoomBlueprint>,
    val options: Options
) {

    val random = Random(seed)

    init {
        if (options.numberOfRooms > 2) {
            val singleDoorRoomCount = blueprints.filter { it.doors.size == 1 }.count()
            val multiDoorRoomCount = blueprints.filter { it.doors.size >= 2 }.count()

            if (singleDoorRoomCount == 0 || multiDoorRoomCount == 0) {
                throw IllegalArgumentException("For that number of rooms, you need at least one single- and one multi-door room blueprint.")
            }
        }

        val possibleNumberOfRooms = this.calculatePossibleNumberOfRooms()
        if (possibleNumberOfRooms < options.numberOfRooms) {
            throw IllegalArgumentException("With these blueprints, we can't reach given number of rooms. ($possibleNumberOfRooms < ${options.numberOfRooms})")
        }
    }

    abstract fun generate(): List<DungeonRoom>

    /**
     * Calculate, if we can reach the [Options.numberOfRooms].
     * - If there are rooms with d>=2, we can easily reach numberOfRooms
     * - If there are only rooms with d=1, we can only reach numberOfRooms=d(spawnRoom)
     */
    private fun calculatePossibleNumberOfRooms(): Int {
        if (blueprints.filter { it.doors.size >= 2 }.count() > 0)
            return options.numberOfRooms
        /*return blueprints.filterIsInstance<SpawnRoomBlueprint>().maxOf { it.doors.size }*/
        return 0
    }

    data class Options(val numberOfRooms: Int, val maximumRoomDistance: Int) {

        init {
            if (numberOfRooms <= 1) throw IllegalArgumentException("numberOfRooms (${numberOfRooms}) must be greater than 1.")
            if (maximumRoomDistance < 1) throw IllegalArgumentException("maximumRoomDistance must not be zero.")
            if (maximumRoomDistance >= numberOfRooms) throw IllegalArgumentException("maximumRoomDistance must be less than numberOfRooms ($numberOfRooms)")
        }

    }

}
