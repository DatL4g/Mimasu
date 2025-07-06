package dev.datlag.mimasu.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.tooling.Platform
import dev.datlag.tooling.Platform.isIOS
import dev.datlag.tooling.Platform.isMacOS
import dev.datlag.tooling.Platform.isTVOS
import dev.datlag.tooling.Platform.isWatchOS
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val Platform.isApple: Boolean by lazy {
    isIOS || isTVOS || isWatchOS || isMacOS
}

expect fun Navigation.Video.dialogProperties(): DialogProperties

expect fun Navigation.Login.dialogProperties(): DialogProperties