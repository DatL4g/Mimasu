package dev.datlag.mimasu.core.common

fun Int.normalize(oldMin: Int, oldMax: Int, newMin: Int, newMax: Int): Int {
    val oldRange = oldMax - oldMin
    val newRange = newMax - newMin

    return (this - oldMin) * newRange / oldRange + newMin
}

fun Float.normalize(oldMin: Float, oldMax: Float, newMin: Float, newMax: Float): Float {
    val oldRange = oldMax - oldMin
    val newRange = newMax - newMin

    return (this - oldMin) * newRange / oldRange + newMin
}

fun Long.normalize(oldMin: Long, oldMax: Long, newMin: Long, newMax: Long): Long {
    val oldRange = oldMax - oldMin
    val newRange = newMax - newMin

    return (this - oldMin) * newRange / oldRange + newMin
}

fun Long.toColorIntCompat(): Int {
    val alpha = (this shr 24 and 0xFF).toInt()
    val red = (this shr 16 and 0xFF).toInt()
    val green = (this shr 8 and 0xFF).toInt()
    val blue = (this and 0xFF).toInt()
    return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}