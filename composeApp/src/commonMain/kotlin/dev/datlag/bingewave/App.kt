package dev.datlag.bingewave

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.CombinedPlatformMaterialTheme
import dev.datlag.tooling.compose.platform.PlatformSurface
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import org.kodein.di.DI
import org.kodein.di.compose.withDI

val LocalDarkMode = compositionLocalOf<Boolean> { error("No dark mode state provided") }
val LocalEdgeToEdge = staticCompositionLocalOf<Boolean> { false }
val LocalHaze = compositionLocalOf<HazeState> { error("No Haze state provided") }

@NonRestartableComposable
@Composable
fun App(
    di: DI,
    systemDarkTheme: Boolean = isSystemInDarkTheme() || Platform.rememberIsTv(),
    content: @Composable () -> Unit
) = withDI(di) {
    CompositionLocalProvider(
        LocalDarkMode provides systemDarkTheme,
    ) {
        CombinedPlatformMaterialTheme(
            // colorScheme = if (systemDarkTheme)
        ) {
            PlatformSurface(
                modifier = Modifier.fillMaxSize(),
                containerColor = Platform.colorScheme().background,
                contentColor = Platform.colorScheme().onBackground,
                content = content
            )
        }
    }
}