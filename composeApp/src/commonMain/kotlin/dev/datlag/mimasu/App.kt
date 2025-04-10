package dev.datlag.mimasu

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.ui.theme.Colors
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
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
    fetchingContent: @Composable () -> Unit = { },
    failureContent: @Composable (NetworkModule.Config.Failure) -> Unit = { },
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
                val accountViewModel = accountViewModel()
                val config by NetworkModule.config.collectAsStateWithLifecycle()

                LaunchedEffect(accountViewModel) {
                    // Force account loading, while startup
                    accountViewModel.isSignedIn
                }

                when (val current = config) {
                    is NetworkModule.Config.Fetching -> fetchingContent()
                    is NetworkModule.Config.Failure -> failureContent(current)
                    is NetworkModule.Config.Success -> content()
                }
            }
        }
    }
}