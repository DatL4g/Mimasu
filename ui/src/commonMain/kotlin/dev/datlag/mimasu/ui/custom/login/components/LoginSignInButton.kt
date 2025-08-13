package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.login_new_password
import dev.datlag.mimasu.ui.login_sign_in
import dev.datlag.tooling.compose.platform.PlatformButton
import dev.datlag.tooling.compose.platform.PlatformButtonScale
import dev.datlag.tooling.compose.platform.PlatformText
import io.tolgee.stringResource

@Composable
internal fun LoginSignInButton(
    enabled: Boolean,
    passwordReset: Boolean,
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit,
    onPasswordReset: () -> Unit
) {
    PlatformButton(
        modifier = modifier,
        onClick = {
            if (passwordReset) {
                onPasswordReset()
            } else {
                onSignIn()
            }
        },
        enabled = enabled,
        scale = PlatformButtonScale.default(
            scale = 1F,
            focusedScale = 1F
        )
    ) {
        if (passwordReset) {
            PlatformText(text = uiStringRes(UiRes.string.login_new_password))
        } else {
            PlatformText(text = uiStringRes(UiRes.string.login_sign_in))
        }
    }
}