package dev.datlag.mimasu.ui.navigation.screen.initial.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.tmdb.model.TrendingWindow
import dev.datlag.mimasu.tv.screen.home.TvHomeScreen
import dev.datlag.tooling.decompose.ioScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.kodein.di.DI
import org.kodein.di.instance
import dev.datlag.mimasu.other.PackageResolver
import dev.datlag.mimasu.tv.PackageAware

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val packageResolver: PackageResolver = di.packageResolver()
): HomeComponent, ComponentContext by componentContext, PackageAware by packageResolver {

    private val tmdb by instance<TMDB>()

    private val _trendingMoviesWindow = MutableStateFlow<TrendingWindow>(TrendingWindow.Day)
    val trendingMoviesWindow: StateFlow<TrendingWindow> = _trendingMoviesWindow

    private val _trendingShowsWindow = MutableStateFlow<TrendingWindow>(TrendingWindow.Day)
    val trendingShowsWindow: StateFlow<TrendingWindow> = _trendingShowsWindow

    override val trendingDayMovies = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.trending.MoviesPaging(TrendingWindow.Day)
    }.flow.cachedIn(ioScope())

    override val trendingWeekMovies = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.trending.MoviesPaging(TrendingWindow.Week)
    }.flow.cachedIn(ioScope())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val trendingMovies = _trendingMoviesWindow.flatMapLatest { window ->
        when (window) {
            is TrendingWindow.Day -> trendingDayMovies
            is TrendingWindow.Week -> trendingWeekMovies
        }
    }.cachedIn(ioScope())

    override val trendingDayShows = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.trending.TVPaging(TrendingWindow.Day)
    }.flow.cachedIn(ioScope())

    override val trendingWeekShows = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.trending.TVPaging(TrendingWindow.Week)
    }.flow.cachedIn(ioScope())

    override val popularMovies = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.popular.MoviesPaging()
    }.flow.cachedIn(ioScope())

    override val popularShows = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.popular.TVPaging()
    }.flow.cachedIn(ioScope())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val trendingShows = _trendingShowsWindow.flatMapLatest { window ->
        when (window) {
            is TrendingWindow.Day -> trendingDayShows
            is TrendingWindow.Week -> trendingWeekShows
        }
    }.cachedIn(ioScope())

    override val trendingPeople = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.trending.PeoplePaging()
    }.flow.cachedIn(ioScope())

    @Composable
    @NonRestartableComposable
    override fun renderCommon() {
        onRender {
            HomeScreen(this)
        }
    }

    @Composable
    @NonRestartableComposable
    override fun renderTv() {
        onRender {
            TvHomeScreen(this)
        }
    }

    companion object {
        private fun DI.packageResolver(): PackageResolver {
            val instance by this.instance<PackageResolver>()
            return instance
        }
    }
}