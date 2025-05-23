package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.login_new_password
import dev.datlag.mimasu.composeapp.generated.resources.login_sign_in
import dev.datlag.tooling.compose.platform.PlatformButton
import dev.datlag.tooling.compose.platform.PlatformText
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginSignInButton(
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
        enabled = enabled
    ) {
        if (passwordReset) {
            PlatformText(text = stringResource(Res.string.login_new_password))
        } else {
            PlatformText(text = stringResource(Res.string.login_sign_in))
        }
    }
}