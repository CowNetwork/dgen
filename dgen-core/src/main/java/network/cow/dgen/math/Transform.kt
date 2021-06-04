package network.cow.dgen.math

/**
 * @author Tobias Büser
 */
enum class Transformation {

    /**
     * No changes to the base class
     */
    IDENTITY,

    /**
     * Rotation 90° clockwise
     */
    ROTATE90, ROTATE180, ROTATE270,

    /**
     * Mirror along the x axis
     */
    MIRRORX, MIRRORY,

}
