package dev.datlag.mimasu.ui.navigation.screen.movie

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.LocalHaze
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.tmdb.api.Trending
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.flow
import org.kodein.di.DI
import org.kodein.di.instance

class MovieScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val trending: Trending.Response.Media.Movie,
    override val visible: Boolean,
    private val onBack: () -> Unit,
    private val onPlay: () -> Unit
) : MovieComponent, ComponentContext by componentContext {

    private val tmdb by instance<TMDB>()

    override val movie = flow {
        emit(tmdb.details.load(trending.id))
    }

    init {
        Napier.e { "Movie ID: ${trending.id}" }
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun renderCommon(scope: SharedTransitionScope) {
        onRender {
            CompositionLocalProvider(
                LocalHaze provides remember { HazeState() }
            ) {
                with(scope) {
                    MovieScreen(this@MovieScreenComponent)
                }
            }
        }
    }

    override fun back() {
        onBack()
    }

    override fun play() {
        onPlay()
    }
}