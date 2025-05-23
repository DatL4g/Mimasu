package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.login_or_login_with
import dev.datlag.tooling.compose.platform.PlatformText
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginSocialProviderDivider(
    hasGitHubProvider: Boolean,
    hasGoogleProvider: Boolean,
    modifier: Modifier = Modifier
) {
    if (hasGitHubProvider || hasGoogleProvider) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1F)
            )
            PlatformText(text = stringResource(Res.string.login_or_login_with))
            HorizontalDivider(
                modifier = Modifier.weight(1F)
            )
        }
    }
}