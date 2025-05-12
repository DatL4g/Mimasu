package dev.datlag.mimasu.ui.common

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

internal actual fun ImageBitmap(
    pixels: IntArray,
    width: Int,
    height: Int
): ImageBitmap {
    return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
}