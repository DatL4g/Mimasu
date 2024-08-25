package dev.datlag.mimasu.ui.navigation.screen.login

import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.ui.navigation.Component
import kotlinx.coroutines.flow.StateFlow

interface LoginComponent : Component {
    val emailInput: StateFlow<String>
    val passwordInput: StateFlow<String>

    val googleAuthProvider: FirebaseGoogleAuthProvider?
    val githubAuthProvider: FirebaseGitHubAuthProvider?

    val emailPasswordEnabled: StateFlow<Boolean>
    val googleEnabled: StateFlow<Boolean>
    val githubEnabled: StateFlow<Boolean>

    fun updateEmail(input: String)
    fun updatePassword(input: String)

    fun googleLogin()
    fun githubLogin(params: GitHubAuthParams)
}