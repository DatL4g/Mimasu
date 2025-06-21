package dev.datlag.mimasu.ui.custom.swipe

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.datlag.mimasu.ui.custom.MaterialSymbols

data class SwipeAction(
    val onSwipe: () -> Unit,
    val icon: @Composable () -> Unit,
    val background: Color,
    val weight: Double = 1.0,
    val isUndo: Boolean = false
) {

    companion object {
        operator fun invoke(
            onSwipe: () -> Unit,
            icon: String,
            background: Color,
            weight: Double = 1.0,
            isUndo: Boolean = false
        ) = SwipeAction(
            onSwipe = onSwipe,
            icon = {
                MaterialSymbols(
                    name = icon,
                    contentDescription = null
                )
            },
            background = background,
            weight = weight,
            isUndo = isUndo
        )
    }
}