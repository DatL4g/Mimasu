package dev.datlag.mimasu

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.ui.theme.Colors
import dev.datlag.mimasu.ui.theme.dynamicDark
import dev.datlag.mimasu.ui.theme.dynamicLight
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.CombinedPlatformMaterialTheme
import dev.datlag.tooling.compose.platform.PlatformSurface
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
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
    fetchingContent: @Composable () -> Unit = { },
    failureContent: @Composable (NetworkModule.Config.Failure) -> Unit = { },
    maintenanceContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit
) = withDI(di) {
    CompositionLocalProvider(
        LocalDarkMode provides systemDarkTheme,
    ) {
        CombinedPlatformMaterialTheme(
            colorScheme = if (systemDarkTheme) Colors.getDarkScheme() else Colors.getLightScheme()
        ) {
            PlatformSurface(
                modifier = Modifier.fillMaxSize(),
                containerColor = Platform.colorScheme().background,
                contentColor = Platform.colorScheme().onBackground
            ) {
                val config by NetworkModule.config.collectAsStateWithLifecycle()

                when (val current = config) {
                    is NetworkModule.Config.Fetching -> fetchingContent()
                    is NetworkModule.Config.Failure -> failureContent(current)
                    is NetworkModule.Config.Maintenance -> maintenanceContent()
                    is NetworkModule.Config.Success -> content()
                }
            }
        }
    }
}