package dev.datlag.mimasu.firebase.auth.provider.github

import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.gitlive.firebase.auth.OAuthProvider

expect class GitHubAuthParams: Any

abstract class FirebaseGitHubAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource
): FirebaseAuthProvider<GitHubAuthParams>(firebaseAuthDataSource) {

    val provider = OAuthProvider(
        provider = "github.com",
        scopes = listOf("read:user")
    )
}