package dev.datlag.mimasu.firebase.auth.provider.github

import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.gitlive.firebase.auth.OAuthProvider

abstract class FirebaseGitHubAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource
): FirebaseAuthProvider<GitHubAuthParams>(firebaseAuthDataSource) {

    val provider = OAuthProvider(
        provider = "github.com",
        scopes = listOf("read:user", "user:email")
    )

    abstract suspend fun link(params: GitHubAuthParams): Result<User>
}