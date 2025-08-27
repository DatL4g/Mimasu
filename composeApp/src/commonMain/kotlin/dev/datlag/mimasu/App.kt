package dev.datlag.mimasu

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.LocalDarkMode
import dev.datlag.mimasu.ui.custom.FailureConfigState
import dev.datlag.mimasu.ui.custom.FetchConfigState
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.other.Network
import dev.datlag.mimasu.ui.theme.Colors
import dev.datlag.mimasu.ui.theme.dynamicDark
import dev.datlag.mimasu.ui.theme.dynamicLight
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.LaunchedVirtualIO
import dev.datlag.tooling.compose.platform.PlatformMaterialTheme
import dev.datlag.tooling.compose.platform.PlatformSurface
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.compose.platform.typography
import org.kodein.di.DI
import org.kodein.di.compose.withDI

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App(
    di: DI,
    typography: Typography = Platform.typography(),
    systemDarkTheme: Boolean = isSystemInDarkTheme() || Platform.rememberIsTv(anyOS = true)
) = withDI(di) {
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
                    val accountViewModel = accountViewModel()
                    val config by Network.config.collectAsStateWithLifecycle()

                    LaunchedVirtualIO(accountViewModel) {
                        // Force account loading, while startup
                        accountViewModel.isSignedIn
                    }

                    when (val current = config) {
                        is Network.Config.Fetching -> FetchConfigState()
                        is Network.Config.Failure -> FailureConfigState(current)
                        is Network.Config.Success -> Navigation()
                    }
                }
            }
        }
    }
}