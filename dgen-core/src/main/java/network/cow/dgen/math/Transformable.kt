package network.cow.dgen.math

/**
 * @author Tobias Büser
 */
interface Transformable<T> {

    /**
     * Transforms the underlying object by given [Transform] and
     * returns the transformed object.
     */
    fun transform(transform: Transform): T

}
