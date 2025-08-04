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
import dev.datlag.mimasu.firebase.auth.api.DisposableDebounce
import dev.datlag.mimasu.firebase.auth.api.GoogleDoH
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.email.FirebaseEmailAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.github.FirebaseGitHubAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider
import dev.datlag.mimasu.firebase.firestore.FirebaseFirestoreWrapper
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.ui.GoogleProvider
import dev.datlag.tooling.Platform
import io.ktor.client.HttpClient
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
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper(
                    authService = service
                )
                val model = AccountViewModel(
                    service = service,
                    firestoreWrapper = wrapper
                )

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf LoginViewModel::class -> {
                val service = di.instanceOrNull<FirebaseAuthService>() ?: FirebaseAuthService()
                val dataSource = di.instanceOrNull<FirebaseAuthDataSource>() ?: FirebaseAuthDataSource(service)
                val httpClient = di.instanceOrNull<HttpClient>()
                val googleDoH = di.instanceOrNull<GoogleDoH>() ?: httpClient?.let(GoogleDoH::create)
                val disposableDebounce = di.instanceOrNull<DisposableDebounce>() ?: httpClient?.let(DisposableDebounce::create)
                val emailProvider = di.instanceOrNull<FirebaseEmailAuthProvider>() ?: FirebaseEmailAuthProvider(
                    firebaseAuthDataSource = dataSource,
                    googleDoH = googleDoH,
                    disposableDebounce = disposableDebounce
                )
                val googleProvider = di.instanceOrNull<FirebaseGoogleAuthProvider>() ?: di.instanceOrNull<GoogleProvider>()?.getOrNull()
                val githubProvider = di.instanceOrNull<FirebaseGitHubAuthProvider>()
                val model = LoginViewModel(
                    directDI = di,
                    service = service,
                    emailAuthProvider = emailProvider,
                    _googleAuthProvider = googleProvider,
                    gitHubAuthProvider = githubProvider
                )

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf ShowViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val service = di.instanceOrNull<FirebaseAuthService>() ?: FirebaseAuthService()
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper(
                    authService = service
                )
                val model = ShowViewModel(
                    detailsRepository = tmdb.details,
                    firestoreWrapper = wrapper
                )

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf FirebaseViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val service = di.instanceOrNull<FirebaseAuthService>() ?: FirebaseAuthService()
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper(
                    authService = service
                )
                val model = FirebaseViewModel(
                    firestoreWrapper = wrapper,
                    detailsRepository = tmdb.details
                )

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf VideoViewModel::class -> {
                val service = di.instanceOrNull<FirebaseAuthService>() ?: FirebaseAuthService()
                val wrapper = di.instanceOrNull<FirebaseFirestoreWrapper>() ?: FirebaseFirestoreWrapper(
                    authService = service
                )
                val model = VideoViewModel(wrapper)

                (model as? T) ?: super.create(modelClass, extras)
            }
            modelClass typeOf DiscoverViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = DiscoverViewModel(discoverRepository = tmdb.discoverRepository)

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
    val kodein by di.instanceOrNull<KodeinViewModelFactory>()
    val factory = kodein ?: run {
        val fallback by di.instanceOrNull<ViewModelProvider.Factory>()
        fallback
    }

    return if (Platform.isJs) {
        viewModel(
            viewModelStoreOwner = viewModelStoreOwner,
            key = key
        ) {
            factory?.create(VM::class, this) ?: error("No Factory found to create ViewModel: $factory")
        }
    } else {
        viewModel<VM>(
            viewModelStoreOwner = viewModelStoreOwner,
            key = key,
            factory = factory,
            extras = extras
        )
    }
}