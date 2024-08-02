package dev.datlag.mimasu.ui.navigation.screen.initial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.datlag.mimasu.ui.custom.WindowSize
import dev.datlag.mimasu.ui.custom.calculateWindowWidthSize
import dev.datlag.mimasu.ui.navigation.screen.initial.component.CompactScreen
import dev.datlag.mimasu.ui.navigation.screen.initial.component.ExpandedScreen
import dev.datlag.mimasu.ui.navigation.screen.initial.component.MediumScreen

@Composable
fun CommonInitialScreen(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (calculateWindowWidthSize()) {
            is WindowSize.Compact -> CompactScreen(content)
            is WindowSize.Medium -> MediumScreen(content)
            is WindowSize.Expanded -> ExpandedScreen(content)
        }
    }
}