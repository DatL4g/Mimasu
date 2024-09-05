package dev.datlag.mimasu.ui.navigation.screen.login

import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.ui.navigation.Component
import kotlinx.coroutines.flow.StateFlow

interface LoginComponent : Component {
    val emailInput: StateFlow<String>
    val passwordInput: StateFlow<String>

    val emailValid: StateFlow<Boolean>
    val passwordValid: StateFlow<Boolean>
    val loginClickable: StateFlow<Boolean>
    val sendClickable: StateFlow<Boolean>
    val emailPasswordFailure: StateFlow<Boolean>

    fun updateEmail(input: String)
    fun updatePassword(input: String)
    fun emailLogin()


    fun googleLogin()
    fun githubLogin(params: GitHubAuthParams)
}