package network.cow.dgen.blueprint

import network.cow.dgen.math.MAX_ROTATION
import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Vector2D

/**
 * A blueprint for defining a dungeon room.
 *
 * Note that we only support 2D shapes here, i.e. every outline
 * and passage points are on the same level.
 *
 * @property name Human readable identifier for this blueprint. Choose a name, that
 * best describes how the blueprint looks like, without looking at the actual form.
 * @property outline The outline is a polygon consisting of the vertices defining
 * the outer shape of the room.
 * @property doors Vectors on the outline specifying the points that can
 * connect to other room blueprints.
 * @property rotation Current clockwise rotation in degrees of this blueprint. To get the
 * origin blueprint, rotate it by -[rotation].
 *
 * @author Tobias Büser
 */
abstract class RoomBlueprint(
    val name: String,
    val outline: Polygon2D,
    val doors: List<Vector2D>,
    rotation: Float = 0f
) {

    val rotation = rotation; get() = field % MAX_ROTATION

    init {
        if (doors.isEmpty()) throw IllegalArgumentException("A room should always contain a door.")

        val noDuplicates = this.outline.vertices.all {
            this.outline.vertices.count { other -> it == other } == 1
        }
        if (!noDuplicates) throw IllegalArgumentException("All vertices have to be unique.")

        val straightLines = this.outline.sides.all {
            it.isVertical || it.isHorizontal
        }
        if (!straightLines) throw IllegalArgumentException("The vertices need to form only straight lines.")

        val doorsOnOutline = this.doors.all {
            this.outline.sides.count { side -> it in side } == 1
        }
        if (!doorsOnOutline) throw IllegalArgumentException("All doors need to be exactly on one side of the outline.")

        val doorsNotAdjacent = this.doors.all {
            !doors.any { other -> it != other && it.isNextTo(other) }
        }
        if (!doorsNotAdjacent) throw IllegalArgumentException("Two doors can not be adjacent.")
    }

    /**
     * Rotates the blueprint and its outline and doors
     * [degrees]° around the origin [Vector2D.ZERO].
     *
     * Default for mathematic operations is counterclockwise, but
     * clockwise is more intuitive, that's why its the default.
     */
    abstract fun rotate(degrees: Float, clockwise: Boolean = true): RoomBlueprint

    /**
     * Shifts the whole blueprint by the given vector.
     */
    abstract fun shift(by: Vector2D): RoomBlueprint

    /**
     * Normalizes the blueprint by shifting it to the origin (0,0).
     *
     * That is especially useful when you want to have relative coordinates
     * instead of absolute.
     */
    fun normalize() = this.shift(Vector2D.ZERO - this.outline.min)

}
