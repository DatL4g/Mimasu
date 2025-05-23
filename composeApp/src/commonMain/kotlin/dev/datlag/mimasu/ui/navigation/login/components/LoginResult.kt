package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.login_failure
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginResult(
    failure: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = failure
    ) {
        PlatformText(
            text = stringResource(Res.string.login_failure),
            color = Platform.colorScheme().error,
            textAlign = TextAlign.Center
        )
    }
}