package dev.datlag.mimasu.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.datlag.mimasu.core.typeOf
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.email.FirebaseEmailAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.ui.GoogleProvider
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import org.kodein.di.instanceOrNull
import kotlin.reflect.KClass

class KodeinViewModelFactory(private val di: DirectDI) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return when {
            modelClass typeOf TrendingViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = TrendingViewModel(trendingRepository = tmdb.trending)

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf MovieListsViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = MovieListsViewModel(movieListsRepository = tmdb.movieLists)

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf TvSeriesListsViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = TvSeriesListsViewModel(tvSeriesListsRepository = tmdb.tvSeriesLists)

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf SearchViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = SearchViewModel(searchRepository = tmdb.search)

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf MovieViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = MovieViewModel(detailsRepository = tmdb.details)

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf PersonViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = PersonViewModel(detailsRepository = tmdb.details)

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf AccountViewModel::class -> {
                val service = di.instanceOrNull<FirebaseAuthService>() ?: FirebaseAuthService()
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper()
                val dataSource = di.instanceOrNull<FirebaseAuthDataSource>() ?: FirebaseAuthDataSource(service)
                val emailProvider = di.instanceOrNull<FirebaseEmailAuthProvider>() ?: FirebaseEmailAuthProvider(dataSource)
                val googleProvider = di.instanceOrNull<FirebaseGoogleAuthProvider>() ?: di.instanceOrNull<GoogleProvider>()?.getOrNull()
                val githubProvider = di.instanceOrNull<FirebaseGitHubAuthProvider>()
                val model = AccountViewModel(
                    directDI = di,
                    service = service,
                    firestoreWrapper = wrapper,
                    emailAuthProvider = emailProvider,
                    _googleAuthProvider = googleProvider,
                    gitHubAuthProvider = githubProvider
                )

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf ShowViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper()
                val model = ShowViewModel(
                    detailsRepository = tmdb.details,
                    firestoreWrapper = wrapper
                )

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf FirebaseViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper()
                val model = FirebaseViewModel(
                    firestoreWrapper = wrapper,
                    detailsRepository = tmdb.details
                )

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf VideoViewModel::class -> {
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper()
                val model = VideoViewModel(wrapper)

                (model as? T) ?: super.create(modelClass, extras)
            }
            else -> platformKodeinViewModelFactory(di, modelClass, extras) ?: super.create(modelClass, extras)
        }
    }
}

expect fun <T : ViewModel> platformKodeinViewModelFactory(
    di: DirectDI,
    modelClass: KClass<T>,
    extras: CreationExtras
): T?

@Composable
inline fun <reified VM : ViewModel> kodeinViewModel(
    di: DI = localDI(),
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    extras: CreationExtras = if (viewModelStoreOwner is HasDefaultViewModelProviderFactory) {
        viewModelStoreOwner.defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }
): VM {
    val factory by di.instanceOrNull<ViewModelProvider.Factory>()

    return viewModel<VM>(
        viewModelStoreOwner = viewModelStoreOwner,
        key = key,
        factory = factory,
        extras = extras
    )
}