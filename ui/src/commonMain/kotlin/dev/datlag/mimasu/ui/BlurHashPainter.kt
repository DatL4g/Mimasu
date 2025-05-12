package dev.datlag.mimasu.ui

import androidx.collection.SparseArrayCompat
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import dev.datlag.mimasu.ui.common.ImageBitmap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.withSign

class BlurHashPainter(
    private val blurHash: String?,
    private var width: Int,
    private var height: Int,
    private val punch: Float = 1F,
    private val scale: Float = 0.1F
) : Painter() {

    private val cacheCosX = SparseArrayCompat<DoubleArray>()
    private val cacheCosY = SparseArrayCompat<DoubleArray>()
    private val charMap = listOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '#', '$', '%', '*', '+', ',',
        '-', '.', ':', ';', '=', '?', '@', '[', ']', '^', '_', '{', '|', '}', '~'
    ).mapIndexed { i, c -> c to i }.toMap()

    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        if (blurHash == null || blurHash.length < 6) {
            return
        }

        val size = 100 * scale
        if (width > height) {
            height = (size * height / width).toInt()
            width = size.toInt()
        } else {
            width = (size * width / height).toInt()
            height = size.toInt()
        }

        val numComponentsEncoded = decode83(blurHash, 0, 1)
        val numComponentsX = (numComponentsEncoded % 9) + 1
        val numComponentsY = (numComponentsEncoded / 9) + 1
        if (blurHash.length != 4 + 2 * numComponentsX * numComponentsY) {
            return
        }

        val maxAcEncoded = decode83(blurHash, 1, 2)
        val maxAc = (maxAcEncoded + 1) / 166F
        val colors = Array(numComponentsX * numComponentsY) { i ->
            if (i == 0) {
                val colorEncoded = decode83(blurHash, 2, 6)
                decodeDc(colorEncoded)
            } else {
                val from = 4 + i * 2
                val colorEncoded = decode83(blurHash, from, from + 2)
                decodeAc(colorEncoded, maxAc * punch)
            }
        }

        val imageArray = IntArray(width * height)
        val calculateCosX = !cacheCosX.containsKey(width * numComponentsX)
        val cosX = getArrayForCosX(calculateCosX, width, numComponentsX)
        val calculateCosY = !cacheCosY.containsKey(height * numComponentsY)
        val cosY = getArrayForCosY(calculateCosY, height, numComponentsY)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var r = 0F
                var g = 0F
                var b = 0F
                for (j in 0 until numComponentsY) {
                    for (i in 0 until numComponentsX) {
                        val cosXValue = cosX.getCos(calculateCosX, i, numComponentsX, x, width)
                        val cosYValue = cosY.getCos(calculateCosY, j, numComponentsY, y, height)
                        val basis = (cosXValue * cosYValue).toFloat()
                        val color = colors[j * numComponentsX + i]
                        r += color[0] * basis
                        g += color[1] * basis
                        b += color[2] * basis
                    }
                }
                imageArray[x + width * y] = Color(
                    red = linearToSrgb(r),
                    green = linearToSrgb(g),
                    blue = linearToSrgb(b)
                ).toArgb()
            }
        }
        drawImage(
            image = ImageBitmap(imageArray, width, height),
            dstSize = IntSize(
                this@onDraw.size.width.roundToInt(),
                this@onDraw.size.height.roundToInt()
            )
        )
    }

    private fun getArrayForCosY(calculate: Boolean, height: Int, numComponentsY: Int) = when {
        calculate -> {
            DoubleArray(height * numComponentsY).also {
                cacheCosY.put(height * numComponentsY, it)
            }
        }
        else -> {
            cacheCosY[height * numComponentsY] ?: DoubleArray(height * numComponentsY).also {
                cacheCosY.putIfAbsent(height * numComponentsY, it)
            }
        }
    }

    private fun getArrayForCosX(calculate: Boolean, width: Int, numComponentsX: Int) = when {
        calculate -> {
            DoubleArray(width * numComponentsX).also {
                cacheCosX.put(width * numComponentsX, it)
            }
        }
        else -> cacheCosX[width * numComponentsX] ?: DoubleArray(width * numComponentsX).also {
            cacheCosX.putIfAbsent(width * numComponentsX, it)
        }
    }

    private fun decode83(value: String, from: Int = 0, to: Int = value.length): Int {
        var result = 0
        for (i in from until to) {
            val index = charMap[value[i]] ?: -1
            if (index != -1) {
                result = result * 83 + index
            }
        }
        return result
    }

    private fun decodeDc(colorEncoding: Int): FloatArray {
        val r = colorEncoding shr 16
        val g = (colorEncoding shr 8) and 255
        val b = colorEncoding and 255
        return floatArrayOf(srgbToLinear(r), srgbToLinear(g), srgbToLinear(b))
    }

    private fun srgbToLinear(colorEncoding: Int): Float {
        val v = colorEncoding / 255F
        return if (v <= 0.04045F) {
            (v / 12.92F)
        } else {
            ((v + 0.055F) / 1.055F).pow(2.4F)
        }
    }

    private fun decodeAc(value: Int, maxAc: Float): FloatArray {
        val r = value / (19 * 19)
        val g = (value / 19) % 19
        val b = value % 19
        return floatArrayOf(
            signedPow2((r - 9) / 9.0F) * maxAc,
            signedPow2((g - 9) / 9.0F) * maxAc,
            signedPow2((b - 9) / 9.0F) * maxAc
        )
    }

    private fun signedPow2(value: Float) = value.pow(2F).withSign(value)

    private fun linearToSrgb(value: Float): Int {
        val v = value.coerceIn(0F, 1F)
        return if (v <= 0.0031308F) {
            (v * 12.92F * 255F + 0.5F).toInt()
        } else {
            ((1.055F * v.pow(1 / 2.4F) - 0.055F) * 255 + 0.5F).toInt()
        }
    }

    private fun DoubleArray.getCos(
        calculate: Boolean,
        x: Int,
        numComponents: Int,
        y: Int,
        size: Int
    ): Double {
        if (calculate) {
            this[x + numComponents * y] = cos(PI * y * x / size)
        }
        return this[x + numComponents * y]
    }
}