package dev.datlag.mimasu.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun Colors.dynamicDark(): ColorScheme {
    return Colors.getDarkScheme()
}

@Composable
actual fun Colors.dynamicLight(): ColorScheme {
    return Colors.getLightScheme()
}