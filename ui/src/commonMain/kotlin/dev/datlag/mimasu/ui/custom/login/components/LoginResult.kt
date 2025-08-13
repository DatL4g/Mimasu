package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.tolgeeInstance
import dev.datlag.mimasu.ui.login_email_disposable
import dev.datlag.mimasu.ui.login_failure
import dev.datlag.mimasu.ui.viewmodel.LoginViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import io.tolgee.stringResource

@Composable
internal fun LoginResult(
    failure: LoginViewModel.LoginResult,
    modifier: Modifier = Modifier,
) {
    val visible = when (failure) {
        is LoginViewModel.LoginResult.Disposable -> true
        is LoginViewModel.LoginResult.Finish -> !failure.success
        else -> false
    }
    AnimatedVisibility(
        modifier = modifier,
        visible = visible
    ) {
        PlatformText(
            text = if (failure is LoginViewModel.LoginResult.Disposable) {
                stringResource(tolgeeInstance(), UiRes.string.login_email_disposable)
            } else {
                stringResource(tolgeeInstance(), UiRes.string.login_failure)
            },
            color = Platform.colorScheme().error,
            textAlign = TextAlign.Center
        )
    }
}