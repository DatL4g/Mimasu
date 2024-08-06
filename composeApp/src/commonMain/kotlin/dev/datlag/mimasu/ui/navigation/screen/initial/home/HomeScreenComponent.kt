package dev.datlag.mimasu.ui.navigation.screen.initial.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import app.cash.paging.Pager
import com.arkivanov.decompose.ComponentContext
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import dev.datlag.mimasu.common.default
import dev.datlag.mimasu.common.localized
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.tmdb.model.TrendingWindow
import dev.datlag.mimasu.tv.screen.home.TvHomeScreen
import dev.datlag.tooling.decompose.ioScope
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.instance

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
): HomeComponent, ComponentContext by componentContext {

    private val tmdb by instance<TMDB>()

    override val trendingMovies = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.trending.MoviesPaging()
    }.flow.cachedIn(ioScope())

    override val trendingSeries = Pager(
        config = PagingConfig(1)
    ) {
        tmdb.trending.TVPaging()
    }.flow.cachedIn(ioScope())

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
}