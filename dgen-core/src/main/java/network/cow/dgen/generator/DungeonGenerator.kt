package network.cow.dgen.generator

import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.blueprint.SpawnRoomBlueprint
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
            val singlePassageRoomCount = blueprints.filter { it.passagePoints.size == 1 }.count()
            val multiPassageRoomCount = blueprints.filter { it.passagePoints.size >= 2 }.count()

            if (singlePassageRoomCount == 0 || multiPassageRoomCount == 0) {
                throw IllegalArgumentException("For that number of rooms, you need at least one single- and one multi-passage room blueprint.")
            }
        }

        val possibleNumberOfRooms = this.calculatePossibleNumberOfRooms()
        if (possibleNumberOfRooms < options.numberOfRooms) {
            throw IllegalArgumentException("With these blueprints, we can't reach given number of rooms. ($possibleNumberOfRooms < ${options.numberOfRooms})")
        }
    }

    abstract fun generate(): List<network.cow.dgen.DungeonRoom>

    /**
     * Calculate, if we can reach the [Options.numberOfRooms].
     * - If there are rooms with pp>=2, we can easily reach numberOfRooms
     * - If there are only rooms with pp=1, we can only reach numberOfRooms=pp(spawnRoom)
     */
    private fun calculatePossibleNumberOfRooms(): Int {
        if (blueprints.filter { it.passagePoints.size >= 2 }.count() > 0)
            return options.numberOfRooms
        return blueprints.filterIsInstance<SpawnRoomBlueprint>().maxOf { it.passagePoints.size }
    }

    data class Options(val numberOfRooms: Int, val maximumRoomDistance: Int) {

        init {
            if (numberOfRooms <= 1) throw IllegalArgumentException("numberOfRooms (${numberOfRooms}) must be greater than 1.")
            if (maximumRoomDistance < 1) throw IllegalArgumentException("maximumRoomDistance must not be zero.")
            if (maximumRoomDistance >= numberOfRooms) throw IllegalArgumentException("maximumRoomDistance must be less than numberOfRooms ($numberOfRooms)")
        }

    }

}
