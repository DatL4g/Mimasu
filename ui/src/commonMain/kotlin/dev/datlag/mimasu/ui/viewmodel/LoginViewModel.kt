package dev.datlag.mimasu.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.api.DisposableDebounce
import dev.datlag.mimasu.firebase.auth.provider.email.EmailAuthParams
import dev.datlag.mimasu.firebase.auth.provider.email.FirebaseEmailAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams
import dev.datlag.mimasu.ui.GoogleProvider
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.DirectDIAware
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull
import org.kodein.di.providerOrNull

class LoginViewModel(
    override val directDI: DirectDI,
    private val service: FirebaseAuthService,
    private val emailAuthProvider: FirebaseEmailAuthProvider,
    private val _googleAuthProvider: FirebaseGoogleAuthProvider?,
    private val gitHubAuthProvider: FirebaseGitHubAuthProvider?,
) : ViewModel(), DirectDIAware {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _emailReadonly = MutableStateFlow(false)
    val emailReadonly = _emailReadonly.asStateFlow()

    private val emailAddressRegex = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    )

    private val passwordLowercaseCharRegex = Regex("[a-z]+")
    private val passwordUppercaseCharRegex = Regex("[A-Z]+")
    private val passwordNumberRegex = Regex("[0-9]+")
    private val passwordSpecialCharRegex = Regex("[\\^$*.\\[\\]{}()?\"!@#%&/,><':;|_~]+")

    @OptIn(ExperimentalCoroutinesApi::class)
    val emailHasError = email.mapLatest {
        if (it.isNotEmpty()) { // using empty as whitespaces not allowed, but empty is not an error
            !it.matches(emailAddressRegex)
        } else {
            false
        }
    }

    fun updateEmail(value: String) {
        _email.update { value.trim() }
    }

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val passwordErrorState = password.mapLatest {
        if (it.isNotEmpty()) { // using empty as whitespaces not allowed, but empty is not an error
            PasswordErrorState(
                hasLowercaseLetter = it.contains(passwordLowercaseCharRegex),
                hasUppercaseLetter = it.contains(passwordUppercaseCharRegex),
                hasNumber = it.contains(passwordNumberRegex),
                hasSpecialChar = it.contains(passwordSpecialCharRegex),
                isLongEnough = it.length >= 8,
                isTooLong = it.length > 4096
            )
        } else {
            null
        }
    }

    val passwordResetCode = Companion.passwordResetCode
    private val _passwordResetUi = MutableStateFlow(false)
    val passwordResetUi = _passwordResetUi.asStateFlow()

    fun updatePassword(value: String) {
        _password.update { value.trim() }
    }

    private var existingGoogleAuthProvider: FirebaseGoogleAuthProvider? = _googleAuthProvider
    private val googleAuthProvider
        get() = existingGoogleAuthProvider
            ?: provideInstance<FirebaseGoogleAuthProvider>()?.also { existingGoogleAuthProvider = it }
            ?: provideInstance<GoogleProvider>()?.getOrNull()?.also { existingGoogleAuthProvider = it }

    val hasGoogleProvider: Boolean
        get() = googleAuthProvider != null

    val hasGitHubProvider: Boolean
        get() = gitHubAuthProvider != null

    private var loginJob: Job? = null
    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult = _loginResult.asStateFlow()

    private fun startLoginJob(block: suspend CoroutineScope.() -> Unit): Job? {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            block()
        }
        return loginJob
    }

    fun emailSignIn(params: EmailAuthParams, onSuccess: suspend () -> Unit) = startLoginJob {
        _loginResult.update { LoginResult.None }

        val emailValid = suspendCatching {
            emailAuthProvider.emailValidity(params.email)
        }.getOrNull() ?: false

        if (emailValid) {
            emailAuthProvider.signIn(params).also { result ->
                _loginResult.update { LoginResult.Finish(result.isSuccess) }
                if (result.isSuccess) {
                    onSuccess()
                }
            }
        } else {
            _loginResult.update { LoginResult.Disposable }
        }
    }

    fun googleSignIn(params: GoogleAuthParams, onSuccess: suspend () -> Unit) = startLoginJob {
        _loginResult.update { LoginResult.None }
        googleAuthProvider?.signIn(params).also { result ->
            _loginResult.update { result?.let { LoginResult.Finish(it.isSuccess) } ?: LoginResult.None }
            if (result?.isSuccess == true) {
                onSuccess()
            }
        }
    }

    fun googleLink(params: GoogleAuthParams) = startLoginJob {
        googleAuthProvider?.link(params)
    }

    fun githubSignIn(params: GitHubAuthParams, onSuccess: suspend () -> Unit) = startLoginJob {
        _loginResult.update { LoginResult.None }
        gitHubAuthProvider?.signIn(params).also { result ->
            _loginResult.update { result?.let { LoginResult.Finish(it.isSuccess) } ?: LoginResult.None }
            if (result?.isSuccess == true) {
                onSuccess()
            }
        }
    }

    fun githubLink(params: GitHubAuthParams) = startLoginJob {
        gitHubAuthProvider?.link(params)
    }

    fun signOut() = startLoginJob {
        service.signOut()
    }

    fun resetPassword(email: String) = startLoginJob {
        suspendCatching {
            service.sendPasswordResetEmail(email)
        }
    }

    suspend fun verifyPasswordResetCode(code: String?) {
        val resetEmail = code?.ifBlank { null }?.let {
            suspendCatching {
                service.verifyPasswordResetCode(code)
            }.getOrNull()?.ifBlank { null }
        }

        if (resetEmail.isNullOrBlank()) {
            _emailReadonly.update { false }
            _passwordResetUi.update { false }
        } else {
            _emailReadonly.update { true }
            _email.update { resetEmail }
            _passwordResetUi.update { true }
        }
    }

    fun changePassword(
        code: String?,
        email: String,
        newPassword: String,
        onSuccess: suspend CoroutineScope.() -> Unit
    ) = startLoginJob {
        if (code.isNullOrBlank()) {
            return@startLoginJob
        }

        suspendCatching {
            service.changePassword(code, newPassword)
        }.onSuccess {
            emailSignIn(
                params = EmailAuthParams(
                    email = email,
                    password = newPassword
                ),
                onSuccess = { onSuccess() }
            )
        }
    }

    private inline fun <reified T : Any> provideInstance(): T? {
        return instanceOrNull<T>()
            ?: providerOrNull<T>()?.invoke()
    }

    override fun onCleared() {
        super.onCleared()

        viewModelScope.cancel()
    }

    @Serializable
    data class PasswordErrorState(
        val hasLowercaseLetter: Boolean,
        val hasUppercaseLetter: Boolean,
        val hasNumber: Boolean,
        val hasSpecialChar: Boolean,
        val isLongEnough: Boolean,
        val isTooLong: Boolean
    ) {
        val hasError: Boolean = !hasLowercaseLetter
                || !hasUppercaseLetter
                || !hasNumber
                || !hasSpecialChar
                || !isLongEnough
                || isTooLong
    }

    @Serializable
    sealed interface LoginResult {

        @Serializable
        data object None : LoginResult

        @Serializable
        data object Disposable : LoginResult

        @Serializable
        data class Finish(val success: Boolean) : LoginResult
    }

    companion object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()

        private val passwordResetCode = MutableStateFlow<String?>(null)

        fun setResetCode(code: String?) = passwordResetCode.update { code?.ifBlank { null } }
    }
}

@Composable
fun loginViewModel(
    di: DI = localDI(),
    viewModelStoreOwner: ViewModelStoreOwner = LoginViewModel.Companion,
    key: String? = null,
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }
) = kodeinViewModel<LoginViewModel>(
    di = di,
    viewModelStoreOwner = viewModelStoreOwner,
    key = key,
    extras = extras
)