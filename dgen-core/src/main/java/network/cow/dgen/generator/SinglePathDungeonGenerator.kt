package network.cow.dgen.generator

import network.cow.dgen.DungeonFinalRoom
import network.cow.dgen.DungeonNormalRoom
import network.cow.dgen.DungeonRoom
import network.cow.dgen.DungeonSpawnRoom
import network.cow.dgen.blueprint.FinalRoomBlueprint
import network.cow.dgen.blueprint.NormalRoomBlueprint
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.blueprint.SpawnRoomBlueprint
import network.cow.dgen.generateHex
import kotlin.random.Random

/**
 * @author Tobias Büser
 */
class SinglePathDungeonGenerator(
    seed: Long, blueprints: List<RoomBlueprint>,
    pathLength: Int
) : DungeonGenerator(seed, blueprints, Options(pathLength + 1, pathLength)) {

    private val random = Random(seed)
    private val spawnRoomBlueprint = blueprints.filterIsInstance<SpawnRoomBlueprint>().random(random)

    companion object {
        private const val MAX_ITERATIONS = 100
    }

    override fun generate(): List<DungeonRoom> {
        val generatedRooms = mutableMapOf<String, DungeonRoom>()

        val spawnRoom = DungeonSpawnRoom(
            generateHex(8, random), 0,
            this.spawnRoomBlueprint,
            mutableMapOf()
        )
        generatedRooms[spawnRoom.id] = spawnRoom

        var iterations = 0

        val currentRooms = mutableListOf<DungeonRoom>(spawnRoom)
        val newCurrentRooms = mutableListOf<DungeonRoom>()
        while (currentRooms.isNotEmpty() && iterations < MAX_ITERATIONS) {
            for (currentRoom in currentRooms) {
                newCurrentRooms.addAll(this.populate(currentRoom, generatedRooms))
            }

            currentRooms.clear()
            currentRooms.addAll(newCurrentRooms)
            newCurrentRooms.clear()

            iterations++
        }

        return generatedRooms.values.toList()
    }

    /**
     * Populates given [room] by finding a new [DungeonRoom] that can be
     * connected to it.
     *
     * If we already reached the [DungeonGenerator.Options.numberOfRooms] or
     * [DungeonGenerator.Options.maximumRoomDistance], we finish by returning
     * an empty list.
     *
     * Otherwise we try to find a fit, that fulfills following conditions:
     * - If we have reached the maximum distance - 1 => It has to be a [FinalRoomBlueprint].
     * - If we have not => It has to be a [NormalRoomBlueprint] with at least 2 passage points.
     *
     * If we don't find one, we also just return an empty list.
     */
    private fun populate(room: DungeonRoom, generatedRooms: MutableMap<String, DungeonRoom>): List<DungeonRoom> {
        if (generatedRooms.size == options.numberOfRooms) return emptyList()
        if (room.depth >= options.maximumRoomDistance) return emptyList()

        val possibleFits = findAllFits(room, generatedRooms)
        if (possibleFits.isEmpty()) return emptyList()

        if (options.maximumRoomDistance - room.depth == 1) {
            // find final room blueprint, if exists
            val finalRoomFit = possibleFits.filter { it.blueprint is FinalRoomBlueprint }
                .randomOrNull(random) ?: return emptyList()

            val finalRoom = DungeonFinalRoom(
                generateHex(8, random),
                room.depth + 1,
                finalRoomFit.blueprint as FinalRoomBlueprint
            )

            this.connect(room, finalRoomFit.index, finalRoomFit.otherIndex, finalRoom)
            generatedRooms[finalRoom.id] = finalRoom
            return listOf(finalRoom)
        }

        // add a room with pp>=2
        val newRoomFit = possibleFits.filter { it.blueprint is NormalRoomBlueprint }
            .filter { it.blueprint.passagePoints.size >= 2 }
            .randomOrNull(random) ?: return emptyList()

        val newRoom = DungeonNormalRoom(
            generateHex(8, random),
            room.depth + 1,
            newRoomFit.blueprint as NormalRoomBlueprint
        )
        this.connect(room, newRoomFit.index, newRoomFit.otherIndex, newRoom)
        generatedRooms[newRoom.id] = newRoom
        return listOf(newRoom)
    }

    private fun connect(
        room: DungeonRoom, passageIndex: Int,
        otherPassageIndex: Int, otherRoom: DungeonRoom
    ) {
        room.passages[passageIndex] = otherRoom.id
        otherRoom.passages[otherPassageIndex] = room.id
    }

    private fun findAllFits(room: DungeonRoom, generatedRooms: MutableMap<String, DungeonRoom>): List<RoomBlueprint.PossibleFit> {
        val fits = mutableListOf<RoomBlueprint.PossibleFit>()

        room.blueprint.passagePoints.forEach { passagePoint ->
            // find possible room for this passage
            val rawFits = room.blueprint.findAllFits(blueprints, passagePoint).filter { it.blueprint !is SpawnRoomBlueprint }
            val possibleFits = rawFits.filter { fit ->
                // check that these fits do not overlap with already existing rooms
                !generatedRooms.values.any { generatedRoom -> fit.blueprint.outline.overlapsWith(generatedRoom.blueprint.outline) }
            }

            fits.addAll(possibleFits)
        }

        return fits
    }

}
