package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.github
import dev.datlag.mimasu.composeapp.generated.resources.google
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.ui.custom.GitHubButton
import dev.datlag.mimasu.ui.custom.GoogleButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginSocialProvider(
    hasGitHubProvider: Boolean,
    hasGoogleProvider: Boolean,
    modifier: Modifier = Modifier,
    onGitHubClicked: (GitHubAuthParams) -> Unit,
    onGoogleClicked: () -> Unit,
) {
    FlowRow(
        modifier = modifier,
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        if (hasGitHubProvider) {
            GitHubButton(
                modifier = Modifier.weight(1F),
                onClick = onGitHubClicked,
                text = stringResource(Res.string.github)
            )
        }
        if (hasGoogleProvider) {
            GoogleButton(
                modifier = Modifier.weight(1F),
                onClick = onGoogleClicked,
                text = stringResource(Res.string.google)
            )
        }
    }
}