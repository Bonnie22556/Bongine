package org.bon26.engine

import kotlin.math.floor
import kotlin.random.Random

enum class NoiseType {
    VALUE, SIMPLEX
}

interface NoiseGenerator {
    fun getNoise(x: Double, y: Double): Double
}

// Value Noise
class ValueNoise(seed: Int = 0): NoiseGenerator {
    private val permutation = IntArray(512) { it % 256 }

    init {
        val rnd = Random(seed)
        permutation.shuffle(rnd)
        permutation.copyInto(permutation, 256, 0, 256)
    }

    private fun smooth(t: Double): Double {
        return t * t * t * (t * (t * 6 - 15) + 10)
    }

    private fun lerp(a: Double, b: Double, t: Double): Double {
        return a + t * (b - a)
    }

    override fun getNoise(x: Double, y: Double): Double {
        val xi = floor(x).toInt() and 255
        val yi = floor(y).toInt() and 255

        val xf = x - floor(x)
        val yf = y - floor(y)

        val aa = permutation[permutation[xi] + yi]
        val ab = permutation[permutation[xi] + yi + 1]
        val ba = permutation[permutation[xi + 1] + yi]
        val bb = permutation[permutation[xi + 1] + yi + 1]

        val u = smooth(xf)
        val v = smooth(yf)

        return lerp(
            lerp(aa / 255.0, ba / 255.0, u),
            lerp(ab / 255.0, bb / 255.0, u),
            v
        )
    }
}

// Simplex Noise
class SimplexNoise(seed: Int = 0): NoiseGenerator {
    private val grad = arrayOf(
        doubleArrayOf(1.0, 1.0), doubleArrayOf(-1.0, 1.0),
        doubleArrayOf(1.0, -1.0), doubleArrayOf(-1.0, -1.0),
        doubleArrayOf(1.0, 0.0), doubleArrayOf(-1.0, 0.0),
        doubleArrayOf(0.0, 1.0), doubleArrayOf(0.0, -1.0)
    )

    private val perm = IntArray(512)

    init {
        val rnd = Random(seed)
        val p = IntArray(256) { it }
        p.shuffle(rnd)
        for (i in 0 until 512) {
            perm[i] = p[i and 255]
        }
    }

    private fun dot(g: DoubleArray, x: Double, y: Double): Double {
        return g[0] * x + g[1] * y
    }

    override fun getNoise(x: Double, y: Double): Double {
        val s = (x + y) * F2
        val i = floor(x + s)
        val j = floor(y + s)

        val t = (i + j) * G2
        val x0 = x - (i - t)
        val y0 = y - (j - t)

        val (i1, j1) = if (x0 > y0) 1 to 0 else 0 to 1

        val x1 = x0 - i1 + G2
        val y1 = y0 - j1 + G2
        val x2 = x0 - 1.0 + 2.0 * G2
        val y2 = y0 - 1.0 + 2.0 * G2

        val ii = i.toInt() and 255
        val jj = j.toInt() and 255

        var t0 = 0.5 - x0 * x0 - y0 * y0
        var t1 = 0.5 - x1 * x1 - y1 * y1
        var t2 = 0.5 - x2 * x2 - y2 * y2

        var n = 0.0

        if (t0 >= 0) {
            val gi0 = perm[ii + perm[jj]] % 8
            n += t0 * t0 * t0 * t0 * dot(grad[gi0], x0, y0)
        }

        if (t1 >= 0) {
            val gi1 = perm[ii + i1 + perm[jj + j1]] % 8
            n += t1 * t1 * t1 * t1 * dot(grad[gi1], x1, y1)
        }

        if (t2 >= 0) {
            val gi2 = perm[ii + 1 + perm[jj + 1]] % 8
            n += t2 * t2 * t2 * t2 * dot(grad[gi2], x2, y2)
        }

        return 70.0 * n
    }

    companion object {
        private const val F2 = 0.36602540378 // (sqrt(3)-1)/2
        private const val G2 = 0.2113248654 // (3-sqrt(3))/6
    }
}

// Fractional Brownian Motion (fBm)
fun fBm(noise: (Double, Double) -> Double, x: Double, y: Double, octaves: Int = 8, lacunarity: Double = 2.0, gain: Double = 0.5): Double {
    var amplitude = 1.0
    var frequency = 1.0
    var total = 0.0
    var maxValue = 0.0

    repeat(octaves) {
        total += noise(x * frequency, y * frequency) * amplitude
        maxValue += amplitude
        amplitude *= gain
        frequency *= lacunarity
    }

    return total / maxValue
}

class Noise(seed: Int = 0, type: NoiseType = NoiseType.SIMPLEX) : NoiseGenerator {
    private val generator: NoiseGenerator = when (type) {
        NoiseType.VALUE -> ValueNoise(seed)
        NoiseType.SIMPLEX -> SimplexNoise(seed)
    }

    override fun getNoise(x: Double, y: Double): Double {
        return generator.getNoise(x, y)
    }

    fun getNoise(x: Float, y: Float): Float {
        return generator.getNoise(x.toDouble(), y.toDouble()).toFloat()
    }

    fun getNoise(x: Int, y: Int): Float {
        return generator.getNoise(x.toDouble(), y.toDouble()).toFloat()
    }

    // Метод для получения fBm с использованием этого генератора шума
    fun fBm(x: Double, y: Double, octaves: Int = 8, lacunarity: Double = 2.0, gain: Double = 0.5): Double {
        return org.bon26.engine.fBm(this::getNoise, x, y, octaves, lacunarity, gain)
    }

    fun fBm(x: Float, y: Float, octaves: Int = 8, lacunarity: Float = 2.0f, gain: Float = 0.5f): Float {
        return fBm(x.toDouble(), y.toDouble(), octaves, lacunarity.toDouble(), gain.toDouble()).toFloat()
    }

    companion object {
        // Статические методы для быстрого доступа
        fun valueNoise(seed: Int = 0): Noise {
            return Noise(seed, NoiseType.VALUE)
        }

        fun simplexNoise(seed: Int = 0): Noise {
            return Noise(seed, NoiseType.SIMPLEX)
        }
    }
}
