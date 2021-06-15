package network.cow.dgen.generator

/**
 * Algorithm to lazily calculate the cartesian product of a list
 * of lists.
 *
 * Completely inspired by: https://stackoverflow.com/a/62270662/11155150
 *
 * @author Tobias Büser
 */
class CartesianProductIterator<T>(vararg val lists: List<T>) : Iterator<List<T>> {

    val expectedProductsSize = lists.map { it.size.toLong() }.reduce(Long::times)

    private var productIndex = 0
    private val lengths = mutableListOf<Int>()
    private val remaining = mutableListOf(1)

    init {
        require(expectedProductsSize <= Int.MAX_VALUE) {
            "Can only produce results whose size does not exceed Int.MAX_VALUE"
        }

        lists.reversed().forEach {
            lengths.add(0, it.size)
            remaining.add(0, it.size * remaining[0])
        }
    }

    override fun hasNext() = productIndex < expectedProductsSize

    override fun next(): List<T> {
        val result = mutableListOf<T>()
        lists.indices.forEach { index ->
            val elementIndex = productIndex / remaining[index] % lengths[index]
            result.add(lists[index][elementIndex])
        }

        productIndex++
        return result
    }

}

