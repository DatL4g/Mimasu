package dev.datlag.mimasu.ui.common

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

internal actual fun ImageBitmap(
    pixels: IntArray,
    width: Int,
    height: Int
): ImageBitmap {
    fun intArrayToByteArrayLittleEndian(array: IntArray = pixels): ByteArray {
        val result = ByteArray(array.size * 4)
        var i = 0
        for (pixel in pixels) {
            result[i++] = (pixel and 0xFF).toByte() // Blue
            result[i++] = ((pixel shr 8) and 0xFF).toByte() // Green
            result[i++] = ((pixel shr 16) and 0xFF).toByte() // Red
            result[i++] = ((pixel shr 24) and 0xFF).toByte() // Alpha
        }
        return result
    }

    val imageInfo = ImageInfo(width, height, ColorType.N32, ColorAlphaType.PREMUL)
    val image = Image.makeRaster(imageInfo, intArrayToByteArrayLittleEndian(), width * 4)
    return image.toComposeImageBitmap()
}