package dev.datlag.mimasu.ui.navigation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import com.arkivanov.decompose.ComponentContext
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import dev.datlag.mimasu.common.default
import dev.datlag.mimasu.common.localized
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.tmdb.model.TrendingWindow
import dev.datlag.mimasu.tmdb.response.Trending
import dev.datlag.mimasu.tv.TvTest
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
): HomeComponent, ComponentContext by componentContext {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(OkHttp) {
        followRedirects = true

        install(ContentNegotiation) {
            json(json, ContentType.Application.Json)
            json(json, ContentType.Text.Plain)
        }
    }

    private val tmdb = TMDB.create(
        apiKey = "",
        client = client
    )

    init {
        launchIO {
            val response = tmdb.trending.all(
                window = TrendingWindow.Day,
                language = Locale.default().localized()
            )

            Napier.e("Status: ${response.status.value}, ${response.status.description}")
        }
    }

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
            TvTest()
        }
    }
}