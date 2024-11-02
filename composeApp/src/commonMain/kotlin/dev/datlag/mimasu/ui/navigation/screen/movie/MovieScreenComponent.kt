package dev.datlag.mimasu.ui.navigation.screen.movie

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.mimasu.tmdb.api.Trending
import org.kodein.di.DI

class MovieScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val trending: Trending.Response.Media.Movie
) : MovieComponent, ComponentContext by componentContext {

    @Composable
    override fun renderCommon() {
        onRender {
            MovieScreen(this)
        }
    }
}