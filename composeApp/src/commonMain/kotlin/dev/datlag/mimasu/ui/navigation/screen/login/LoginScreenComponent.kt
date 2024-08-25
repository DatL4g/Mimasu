package dev.datlag.mimasu.ui.navigation.screen.login

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import org.kodein.di.DI
import org.kodein.di.instanceOrNull

class LoginScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
) : LoginComponent, ComponentContext by componentContext {

    private val _emailInput = MutableStateFlow("")
    override val emailInput: StateFlow<String> = _emailInput.asStateFlow()

    private val _passwordInput = MutableStateFlow("")
    override val passwordInput: StateFlow<String> = _passwordInput.asStateFlow()

    override val googleAuthProvider by instanceOrNull<FirebaseGoogleAuthProvider>()
    override val githubAuthProvider by instanceOrNull<FirebaseGitHubAuthProvider>()

    private val _emailPasswordEnabled = MutableStateFlow(true)
    override val emailPasswordEnabled: StateFlow<Boolean> = _emailPasswordEnabled

    private val _googleEnabled = MutableStateFlow(true)
    override val googleEnabled: StateFlow<Boolean> = _googleEnabled

    private val _githubEnabled = MutableStateFlow(true)
    override val githubEnabled: StateFlow<Boolean> = _githubEnabled

    @Composable
    override fun renderCommon() {
        onRender {
            LoginScreen(this)
        }
    }

    override fun updateEmail(input: String) {
        _emailInput.update { input }
    }

    override fun updatePassword(input: String) {
        _passwordInput.update { input }
    }

    override fun googleLogin() {
        launchIO {
            // signIn **false** indicates that it's the first run, and will try again on failure
            googleAuthProvider?.signIn(false)?.onFailure {
                providerAvailability(it) {
                    _googleEnabled.update { false }
                }
            }?.onSuccess {
                Napier.e("Navigate to HomeScreen")
            }
        }
    }

    override fun githubLogin(params: GitHubAuthParams) {
        launchIO {
            githubAuthProvider?.signIn(params)?.onFailure {
                providerAvailability(it) {
                    _githubEnabled.update { false }
                }
            }?.onSuccess {
                Napier.e("Navigate to HomeScreen")
            }
        }
    }

    private fun providerAvailability(exception: Throwable, onEmpty: () -> Unit) {
        if (exception is FirebaseAuthService.CollisionException) {
            val email = exception.email.ifBlank { null }
            val allProvider = exception.provider
            if (allProvider.isNotEmpty()) {
                val changeEmail = _emailPasswordEnabled.updateAndGet {
                    allProvider.any { p -> p.equals("password", ignoreCase = true) }
                }
                if (changeEmail && !email.isNullOrBlank()) {
                    _emailInput.update { email }
                }
                _googleEnabled.update {
                    allProvider.any { p ->
                        p.equals("google", ignoreCase = true) || p.equals("google.com", ignoreCase = true)
                    }
                }
                _githubEnabled.update {
                    allProvider.any { p ->
                        p.equals("github", ignoreCase = true) || p.equals("github.com", ignoreCase = true)
                    }
                }
            } else {
                onEmpty()
            }
        }
    }
}