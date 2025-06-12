package dev.datlag.mimasu.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.provider.email.EmailAuthParams
import dev.datlag.mimasu.firebase.auth.provider.email.FirebaseEmailAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import dev.datlag.mimasu.ui.GoogleProvider
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
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
    private val service: FirebaseAuthService,
    private val firestoreWrapper: FirebaseFirestoreWrapper,
) : ViewModel() {

    val user = service.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = currentUser
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val userData = user.transformLatest { user ->
        if (user == null) {
            return@transformLatest emit(null)
        } else {
            return@transformLatest emit(firestoreWrapper.getUserData())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    val currentUser: User?
        get() = service.currentUser

    val isSignedIn: Boolean
        get() = currentUser != null

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