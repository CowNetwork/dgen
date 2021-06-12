package network.cow.dgen.blueprint

import network.cow.dgen.math.MAX_ROTATION
import network.cow.dgen.math.Orientation
import network.cow.dgen.math.Polygon2D
import network.cow.dgen.math.Transform
import network.cow.dgen.math.Transformable
import network.cow.dgen.math.Vector2D

/**
 * A blueprint for defining a dungeon room.
 *
 * Note that we only support 2D shapes here, i.e. every outline
 * and passage points are on the same level.
 * Also, we check the blueprint's integrity when initializing.
 *
 * @property name Human readable identifier for this blueprint. Choose a name, that
 * best describes how the blueprint looks like, without looking at the actual form.
 * @property outline The outline is a polygon consisting of the vertices defining
 * the outer shape of the room.
 * @property doors Vectors on the outline specifying the points that can
 * connect to other room blueprints.
 *
 * TODO metric on how good the blueprint is for generation
 * - distance between doors (the more the better) in relation
 * to the general outline's length
 * - count of doors (the more the better)
 * - count of edges (the more the better)
 * -> normalize this metric to be between [0, 1]
 *
 * @author Tobias Büser
 */
open class RoomBlueprint(
    val name: String,
    val outline: Polygon2D,
    val doors: List<Vector2D>,
    val allowedTransforms: List<Transform> = listOf(Transform.IDENTITY),
    rotation: Float = 0f
) : Transformable<RoomBlueprint> {

    val rotation = rotation; get() = field % MAX_ROTATION
    val doorCount = doors.size

    init {
        if (doors.isEmpty()) throw IllegalArgumentException("A room should always contain a door.")
        if (this.outline.vertices.size < 4) throw IllegalArgumentException("The outline needs to have at least 4 vertices.")
        if (this.outline.orientation != Orientation.CLOCKWISE) throw IllegalArgumentException("The outline's vertices need to be in clockwise order.")

        val noDuplicates = this.outline.vertices.all {
            this.outline.vertices.count { other -> it == other } == 1
        }
        if (!noDuplicates) throw IllegalArgumentException("All vertices have to be unique.")

        val noDuplicateDoors = this.doors.all {
            this.doors.count { other -> it == other } == 1
        }
        if (!noDuplicateDoors) throw IllegalArgumentException("All doors have to be unique.")

        val straightLines = this.outline.edges.all {
            it.isVertical || it.isHorizontal
        }
        if (!straightLines) throw IllegalArgumentException("The vertices need to form only straight lines.")

        val doorsOnOutline = this.doors.all {
            this.outline.edges.count { side -> it in side } == 1
        }
        if (!doorsOnOutline) throw IllegalArgumentException("All doors need to be exactly on one side of the outline.")

        val doorsNotAdjacent = this.doors.all {
            !doors.any { other -> it != other && it.isNextTo(other) }
        }
        if (!doorsNotAdjacent) throw IllegalArgumentException("Two doors can not be adjacent.")
    }

    override fun transform(transform: Transform): RoomBlueprint {
        if (transform !in this.allowedTransforms) {
            return this
        }
        val transformedOutline = this.outline.transform(transform)

        return RoomBlueprint(
            this.name,
            transformedOutline,
            doors.map { it.transform(transform) },
            this.allowedTransforms
        )
    }

    /**
     * Shifts the whole blueprint by the given vector.
     */
    fun shift(by: Vector2D): RoomBlueprint {
        return RoomBlueprint(
            this.name,
            this.outline + by,
            this.doors.map { it + by },
            this.allowedTransforms,
            this.rotation
        )
    }

    /**
     * Normalizes the blueprint by shifting it to the origin (0,0).
     *
     * That is especially useful when you want to have relative coordinates
     * instead of absolute.
     */
    fun normalize() = this.shift(Vector2D.ZERO - this.outline.min)


}
