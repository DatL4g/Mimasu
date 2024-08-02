package dev.datlag.mimasu.ui.custom

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
sealed interface WindowSize {

    @Serializable
    data object Compact : WindowSize

    @Serializable
    data object Medium : WindowSize

    @Serializable
    data object Expanded : WindowSize
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun calculateWindowWidthSize(): WindowSize {
    val sizeClass = calculateWindowSizeClass()

    return when (sizeClass.widthSizeClass) {
        WindowWidthSizeClass.Medium -> WindowSize.Medium
        WindowWidthSizeClass.Expanded -> when (sizeClass.heightSizeClass) {
            WindowHeightSizeClass.Compact -> WindowSize.Medium
            else -> WindowSize.Expanded
        }
        else -> WindowSize.Compact
    }
}