package dev.datlag.mimasu.ui.common

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams

@Composable
expect fun rememberGitHubAuthParams(): GitHubAuthParams?

@Composable
expect fun rememberGoogleAuthParams(): GoogleAuthParams