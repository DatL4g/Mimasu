package dev.datlag.mimasu.tv

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Typography
import dev.datlag.mimasu.tv.ui.navigation.Navigation
import dev.datlag.mimasu.tv.ui.theme.getDarkScheme
import dev.datlag.mimasu.tv.ui.theme.getLightScheme
import dev.datlag.mimasu.ui.LocalDarkMode
import dev.datlag.mimasu.ui.custom.FailureConfigState
import dev.datlag.mimasu.ui.custom.FetchConfigState
import dev.datlag.mimasu.ui.other.Network
import dev.datlag.mimasu.ui.theme.Colors
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.LaunchedVirtualIO
import dev.datlag.tooling.compose.platform.rememberIsTv
import org.kodein.di.DI
import org.kodein.di.compose.withDI

@Composable
fun TVApp(
    di: DI,
    appImage: Painter,
    typography: Typography = MaterialTheme.typography,
    systemDarkTheme: Boolean = isSystemInDarkTheme() || Platform.rememberIsTv(anyOS = true)
) = withDI(di) {
    CompositionLocalProvider(
        LocalDarkMode provides systemDarkTheme
    ) {
        MaterialTheme(
            colorScheme = if (systemDarkTheme) Colors.getDarkScheme() else Colors.getLightScheme(),
            typography = typography,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                colors = SurfaceDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                val accountViewModel = accountViewModel()
                val config by Network.config.collectAsState()

                LaunchedVirtualIO(accountViewModel) {
                    // Force account loading, while startup
                    accountViewModel.isSignedIn
                }

                when (val current = config) {
                    is Network.Config.Fetching -> FetchConfigState()
                    is Network.Config.Failure -> FailureConfigState(current)
                    is Network.Config.Success -> Navigation(appImage)
                }
            }
        }
    }
}