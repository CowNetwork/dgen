package network.cow.dgen

import kotlin.random.Random

private const val HEX_SEED_LENGTH = 12

/**
 * Generates a random hexadecimal seed with
 * length [HEX_SEED_LENGTH].
 */
fun generateHexSeed() = generateHex(HEX_SEED_LENGTH)

fun generateHex(length: Int, random: Random = Random.Default): String {
    val allowedChars = ('a'..'f') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random(random) }
        .joinToString("")
}

/**
 * Generates a seed with [generateHexSeed] and translates
 * it into a [Long].
 */
fun generateSeed(): Long {
    return generateHexSeed().toLong(radix = 16)
}
