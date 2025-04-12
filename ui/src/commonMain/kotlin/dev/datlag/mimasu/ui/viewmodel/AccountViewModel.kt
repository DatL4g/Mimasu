package dev.datlag.mimasu.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.provider.email.EmailAuthParams
import dev.datlag.mimasu.firebase.auth.provider.email.FirebaseEmailAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.ui.GoogleProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.DirectDIAware
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull
import org.kodein.di.providerOrNull

class AccountViewModel(
    override val directDI: DirectDI,
    private val service: FirebaseAuthService,
    private val emailAuthProvider: FirebaseEmailAuthProvider,
    private val _googleAuthProvider: FirebaseGoogleAuthProvider?,
    private val gitHubAuthProvider: FirebaseGitHubAuthProvider?
) : ViewModel(), DirectDIAware {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

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

    fun updatePassword(value: String) {
        _password.update { value.trim() }
    }

    val user = service.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = service.currentUser
    )

    val isSignedIn: Boolean
        get() = service.currentUser != null

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

    fun emailSignIn(params: EmailAuthParams) = startLoginJob {
        emailAuthProvider.signIn(params)
    }

    fun googleSignIn() = startLoginJob {
        googleAuthProvider?.signIn(
            FirebaseGoogleAuthProvider.SignInParams(isRetrying = false)
        )
    }

    fun githubSignIn(params: GitHubAuthParams) = startLoginJob {
        gitHubAuthProvider?.signIn(params)
    }

    fun signOut() = startLoginJob {
        service.signOut()
    }

    private fun startLoginJob(block: suspend CoroutineScope.() -> Unit) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            block()
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

    companion object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}

@Composable
fun accountViewModel(
    di: DI = localDI(),
    viewModelStoreOwner: ViewModelStoreOwner = AccountViewModel.Companion,
    key: String? = null,
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }
) = kodeinViewModel<AccountViewModel>(
    di = di,
    viewModelStoreOwner = viewModelStoreOwner,
    key = key,
    extras = extras
)