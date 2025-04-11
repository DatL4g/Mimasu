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
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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