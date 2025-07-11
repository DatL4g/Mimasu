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
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import org.kodein.di.DI
import org.kodein.di.compose.localDI

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

    suspend fun deleteAccount(user: User) {
        firestoreWrapper.deleteUserData(user)

        user.delete()
        service.signOut()
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