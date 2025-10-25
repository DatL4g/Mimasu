package dev.datlag.mimasu.ui.space

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import dev.datlag.mimasu.ui.LocalDarkMode
import dev.datlag.mimasu.ui.theme.Colors
import dev.datlag.mimasu.ui.theme.dynamicDark
import dev.datlag.mimasu.ui.theme.dynamicLight
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformMaterialTheme
import dev.datlag.tooling.compose.platform.PlatformSurface
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.compose.platform.typography

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpaceTheme(
    typography: Typography = Platform.typography(),
    systemDarkTheme: Boolean = isSystemInDarkTheme() || Platform.rememberIsTv(anyOS = true),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDarkMode provides systemDarkTheme
    ) {
        PlatformMaterialTheme(
            colorScheme = if (systemDarkTheme) Colors.dynamicDark() else Colors.dynamicLight(),
            typography = typography
        ) {
            MaterialExpressiveTheme(
                colorScheme = Platform.colorScheme(),
                typography = Platform.typography()
            ) {
                PlatformSurface(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Platform.colorScheme().background,
                    contentColor = Platform.colorScheme().onBackground
                ) {
                    content()
                }
            }
        }
    }
}