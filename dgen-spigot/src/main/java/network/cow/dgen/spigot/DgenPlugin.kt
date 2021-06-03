package network.cow.dgen.spigot

import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.math.Vector3
import com.sk89q.worldedit.math.transform.AffineTransform
import com.sk89q.worldedit.math.transform.CombinedTransform
import com.sk89q.worldedit.session.ClipboardHolder
import network.cow.dgen.DungeonRoom
import network.cow.dgen.blueprint.RoomBlueprint
import network.cow.dgen.generator.SinglePathDungeonGenerator
import network.cow.dgen.room.SpawnRoomBlueprint
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream


/**
 * @author Tobias Büser
 */
class DgenPlugin : JavaPlugin(), CommandExecutor {

    private lateinit var clipboards: Map<String, Clipboard>
    private var blueprints = Cockbitch.BLUEPRINTS.associateBy { it.name }

    override fun onEnable() {
        getCommand("gen")!!.setExecutor(this)

        logger.info("Load schematics ...")
        this.clipboards = this.loadClipboards()
        logger.info("Loaded ${clipboards.size} schematics.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        val dungeonGenerator = SinglePathDungeonGenerator(0, blueprints.values.toList(), 15)
        val dungeon = dungeonGenerator.generate()

        val session = WorldEdit.getInstance().newEditSessionBuilder()
            .world(BukkitAdapter.adapt(sender.world))
            .maxBlocks(-1)
            .build()

        this.placeDungeon(session, dungeon, sender.location.clone().subtract(0.0, 5.0, 0.0))

        sender.sendMessage("§aDungeon pasted.")
        return true
    }

    private fun placeDungeon(
        session: EditSession,
        rooms: List<DungeonRoom>,
        position: Location
    ) {
        val vectorPos = BukkitAdapter.asVector(position).toBlockPoint()
        val roomsMap = rooms.associateBy { it.id }
        val spawnRoom = rooms.first { it.blueprint is SpawnRoomBlueprint }

        val clipboard = clipboards[spawnRoom.blueprint.name]!!

        this.pasteClipboard(
            session,
            clipboard,
            spawnRoom.blueprint,
            spawnRoom.blueprint.rotation.toDouble(),
            0,
            vectorPos
        )

        val passage = spawnRoom.blueprint.doors[0]
        this.placeConnectedRoom(
            null,
            session,
            spawnRoom,
            roomsMap,
            spawnRoom.blueprint,
            vectorPos.subtract(passage.x.toInt(), 0, passage.y.toInt())
        )
    }

    private fun placeConnectedRoom(
        previousRoom: DungeonRoom?,
        session: EditSession,
        currentRoom: DungeonRoom,
        map: Map<String, DungeonRoom>,
        blueprint: RoomBlueprint,
        startPosition: BlockVector3
    ) {
        currentRoom.doors.forEach { (index, id) ->
            // make sure that we dont move backwards
            // by choosing the passage that leads back to the previous room
            if (id == previousRoom?.id) return@forEach

            // the passage point we want to connect the other room to
            val passage = blueprint.doors[index]

            // the position where the other room will move to, so that
            // the other passage point is exactly on this position
            val targetPoint = passage.adjacentVectors(1.0).first { it !in blueprint.outline }

            // the room we want to connect to the target point
            val newRoom = map[id]!!

            // this is the absolute position of the targetPoint
            val newPos = startPosition.add(
                targetPoint.x.toInt(),
                0,
                targetPoint.y.toInt()
            )

            pasteClipboard(
                session,
                clipboards[newRoom.blueprint.name]!!,
                blueprints[newRoom.blueprint.name]!!,
                newRoom.blueprint.rotation.toDouble(),
                newRoom.doors.entries.first { it.value == currentRoom.id }.key,
                newPos
            )

            this.placeConnectedRoom(currentRoom, session, newRoom, map, newRoom.blueprint, startPosition)
        }
    }

    /**
     * Pastes the given clipboard, so that the passage of index
     * [passageIndex] is exactly at position [target].
     */
    private fun pasteClipboard(
        session: EditSession,
        clipboard: Clipboard, normalizedBlueprint: RoomBlueprint,
        rotation: Double, passageIndex: Int, target: BlockVector3
    ) {
        val clipboardHolder = ClipboardHolder(clipboard)
        val transform = AffineTransform().rotateY(rotation)

        val passage = normalizedBlueprint.doors[passageIndex]
        val rotPassage = transform.apply(Vector3.at(passage.x, 0.0, passage.y)).toBlockPoint().add(target)

        val diff = target.subtract(rotPassage)
        clipboardHolder.transform = CombinedTransform(transform, AffineTransform().translate(diff))
        session.use {
            val operation: Operation = clipboardHolder
                .createPaste(it)
                .ignoreAirBlocks(true)
                .to(target)
                .build()
            Operations.complete(operation)
        }
    }

    private fun loadClipboards(): Map<String, Clipboard> {
        val folder = File(dataFolder, "blueprints/")
        return getBlueprintClipboards(folder)
    }

    private fun getBlueprintClipboards(folder: File): Map<String, Clipboard> {
        val blueprintFiles = folder.walk().filter { it.extension == "schem" }

        val blueprintClipboards = mutableMapOf<String, Clipboard>()
        for (blueprintFile in blueprintFiles) {
            val format = ClipboardFormats.findByFile(blueprintFile) ?: continue

            val clipboardReader = format.getReader(FileInputStream(blueprintFile))
            clipboardReader.use {
                val clipboard = it.read() ?: return@use

                clipboard.origin = BlockVector3.ZERO
                clipboard.region.shift(BlockVector3.ZERO.subtract(clipboard.region.minimumPoint))

                blueprintClipboards[blueprintFile.nameWithoutExtension] = clipboard
            }
        }
        return blueprintClipboards
    }

}
