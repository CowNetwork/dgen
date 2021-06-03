package network.cow.dgen.generator

import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.PossibleFit
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.blueprint.findAllFits
import network.cow.dgen.generateHex
import network.cow.dgen.room.DungeonEmptyRoom
import network.cow.dgen.room.DungeonFinalRoom
import network.cow.dgen.room.DungeonSpawnRoom
import network.cow.dgen.room.EmptyRoomBlueprint
import network.cow.dgen.room.FinalRoomBlueprint
import network.cow.dgen.room.SpawnRoomBlueprint

/**
 * This generator works by just attaching one room to another to form a
 * single path dungeon.
 * Note that this is naturally capped for each seed, because at some point we
 * can't add another room to the last one. This could result in a dungeon without
 * a final room, that is why this algorithm should only be used for testing.
 *
 * But maybe an ugly workaround for this issue: Generating so many seeds and their
 * respective dungeons until the path length is reached.
 *
 * @author Tobias Büser
 */
class SinglePathDungeonGenerator(
    seed: Long, blueprints: List<RoomBlueprint>,
    pathLength: Int
) : DungeonGenerator(seed, blueprints, Options(pathLength + 1, pathLength)) {

    private val spawnRoomBlueprint = blueprints.filterIsInstance<SpawnRoomBlueprint>().random(random)

    /**
     * The maximum iterations has to be at least the
     * [DungeonGenerator.Options.numberOfRooms] long.
     * Everything exceeding this limit will be the dungeon trying to find a fit
     * where possibly no fit can be found.
     */
    private val maxIterations = options.numberOfRooms * 2

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
        while (currentRooms.isNotEmpty() && iterations < maxIterations) {
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
            val finalRoomFit = possibleFits.filter { it.other is FinalRoomBlueprint }
                .randomOrNull(random) ?: return emptyList()

            val finalRoom = DungeonFinalRoom(
                generateHex(8, random),
                room.depth + 1,
                finalRoomFit.other as FinalRoomBlueprint
            )

            this.connect(room, finalRoomFit.index, finalRoomFit.otherIndex, finalRoom)
            generatedRooms[finalRoom.id] = finalRoom
            return listOf(finalRoom)
        }

        // add a room with pp>=2
        val newRoomFit = possibleFits.filter { it.other is EmptyRoomBlueprint }
            .filter { it.other.doors.size >= 2 }
            .randomOrNull(random) ?: return emptyList()

        val newRoom = DungeonEmptyRoom(
            generateHex(8, random),
            room.depth + 1,
            newRoomFit.other
        )
        this.connect(room, newRoomFit.index, newRoomFit.otherIndex, newRoom)
        generatedRooms[newRoom.id] = newRoom
        return listOf(newRoom)
    }

    private fun connect(
        room: DungeonRoom, passageIndex: Int,
        otherPassageIndex: Int, otherRoom: DungeonRoom
    ) {
        // TODO
        /*room.doors[passageIndex] = otherRoom.id
        otherRoom.doors[otherPassageIndex] = room.id*/
    }

    private fun findAllFits(
        room: DungeonRoom,
        generatedRooms: MutableMap<String, DungeonRoom>
    ): List<PossibleFit> {
        val fits = mutableListOf<PossibleFit>()

        room.blueprint.doors.forEach { passagePoint ->
            // find possible room for this passage
            val rawFits = room.blueprint.findAllFits(blueprints, passagePoint).filter { it.other !is SpawnRoomBlueprint }
            val possibleFits = rawFits.filter { fit ->
                // check that these fits do not overlap with already existing rooms
                !generatedRooms.values.any { generatedRoom -> fit.other.outline.overlapsWith(generatedRoom.blueprint.outline) }
            }

            fits.addAll(possibleFits)
        }

        return fits
    }

}
