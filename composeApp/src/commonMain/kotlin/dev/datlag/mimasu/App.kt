package dev.datlag.mimasu

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import dev.datlag.mimasu.ui.theme.Colors
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformMaterialTheme
import dev.datlag.tooling.compose.platform.PlatformSurface
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.compose.platform.typography
import org.kodein.di.DI
import org.kodein.di.compose.withDI

val LocalDarkMode = compositionLocalOf<Boolean> { error("No dark mode state provided") }

@Composable
fun App(
    di: DI,
    typography: Typography = Platform.typography(),
    systemDarkTheme: Boolean = isSystemInDarkTheme() || Platform.rememberIsTv(anyOS = true),
    content: @Composable () -> Unit
) = withDI(di) {
    CompositionLocalProvider(
        LocalDarkMode provides systemDarkTheme
    ) {
        PlatformMaterialTheme(
            colorScheme = if (systemDarkTheme) Colors.getDarkScheme() else Colors.getLightScheme(),
            typography = typography
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