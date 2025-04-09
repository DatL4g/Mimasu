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
import dev.datlag.mimasu.tmdb.TMDB
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
            else -> super.create(modelClass, extras)
        }
    }
}

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