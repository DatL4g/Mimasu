package dev.datlag.mimasu.common

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.tooling.Platform

@Composable
expect fun Platform.githubAuthParams(): GitHubAuthParams?