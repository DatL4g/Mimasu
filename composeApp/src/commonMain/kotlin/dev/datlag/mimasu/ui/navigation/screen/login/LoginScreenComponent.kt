package dev.datlag.mimasu.ui.navigation.screen.login

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.provider.email.EmailAuthParams
import dev.datlag.mimasu.firebase.auth.provider.email.FirebaseEmailAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.email.ForgotPasswordParams
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.tooling.compose.withMainContext
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.instanceOrNull

class LoginScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val toHome: () -> Unit
) : LoginComponent, ComponentContext by componentContext {

    private val _emailInput = MutableStateFlow("")
    override val emailInput: StateFlow<String> = _emailInput.asStateFlow()

    private val _passwordInput = MutableStateFlow("")
    override val passwordInput: StateFlow<String> = _passwordInput.asStateFlow()

    private val emailAuthProvider by instance<FirebaseEmailAuthProvider>()
    private val googleAuthProvider by instanceOrNull<FirebaseGoogleAuthProvider>()
    private val githubAuthProvider by instanceOrNull<FirebaseGitHubAuthProvider>()

    private val _emailPasswordFailure = MutableStateFlow(false)
    override val emailPasswordFailure: StateFlow<Boolean> = _emailPasswordFailure

    private val _emailValid = MutableStateFlow(true)
    override val emailValid: StateFlow<Boolean> = _emailValid

    private val _passwordValid = MutableStateFlow(true)
    override val passwordValid: StateFlow<Boolean> = _passwordValid

    private val _loginClickable = MutableStateFlow(false)
    override val loginClickable: StateFlow<Boolean> = _loginClickable

    private val _sendClickable = MutableStateFlow(false)
    override val sendClickable: StateFlow<Boolean> = _sendClickable

    @Composable
    override fun renderCommon() {
        onRender {
            LoginScreen(this)
        }
    }

    override fun updateEmail(input: String) {
        val newEmail = _emailInput.updateAndGet { input.trimStart() }
        val valid = _emailValid.updateAndGet { EMAIL_REGEX.matches(newEmail) }

        updateEmailState(email = newEmail, emailGood = valid)
    }

    override fun updatePassword(input: String) {
        val newPassword = _passwordInput.updateAndGet { input.trimStart() }
        val valid = _passwordValid.updateAndGet { newPassword.length >= 8 }

        updateEmailState(password = newPassword, passwordGood = valid)
    }

    private fun updateEmailState(
        email: String = emailInput.value,
        emailGood: Boolean = emailValid.value,
        password: String = passwordInput.value,
        passwordGood: Boolean = passwordValid.value
    ) {
        _emailPasswordFailure.update { false }
        _loginClickable.update {
            emailGood && passwordGood && email.isNotBlank() && password.isNotBlank()
        }
        _sendClickable.update {
            email.isNotBlank() && emailGood
        }
    }

    override fun emailLogin() {
        launchIO {
            emailAuthProvider.signIn(
                params = EmailAuthParams(
                    email = emailInput.value.trim(),
                    password = passwordInput.value.trim()
                )
            ).onFailure {
                Napier.e("Login failure", it)
                _emailPasswordFailure.update { true }
                providerAvailability(
                    exception = it,
                    noCollision = {

                    },
                    onEmpty = {
                    }
                )
            }.onSuccess {
                withMainContext {
                    toHome()
                }
            }
        }
    }

    override fun googleLogin() {
        launchIO {
            // signIn **false** indicates that it's the first run, and will try again on failure
            googleAuthProvider?.signIn(false)?.onFailure {
                providerAvailability(it) {
                }
            }?.onSuccess {
                withMainContext {
                    toHome()
                }
            }
        }
    }

    override fun githubLogin(params: GitHubAuthParams) {
        launchIO {
            githubAuthProvider?.signIn(params)?.onFailure {
                providerAvailability(it) {
                }
            }?.onSuccess {
                withMainContext {
                    toHome()
                }
            }
        }
    }

    override fun skipLogin() {
        toHome()
    }

    private fun providerAvailability(
        exception: Throwable,
        noCollision: () -> Unit = { },
        onEmpty: () -> Unit
    ) {
        if (exception is FirebaseAuthService.CollisionException) {
            val email = exception.email.ifBlank { null }
            val allProvider = exception.provider
            if (allProvider.isNotEmpty()) {
            } else {
                onEmpty()
            }
        } else {
            noCollision()
        }
    }

    companion object {
        private const val EMAIL_PATTERN = """^\S+@\S+\.\S+$"""
        private val EMAIL_REGEX = Regex(EMAIL_PATTERN, setOf(RegexOption.IGNORE_CASE))
    }
}