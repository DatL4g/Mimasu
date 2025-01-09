package dev.datlag.mimasu.ui.navigation.screen.movie

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.child
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.LocalAnimatedVisibilityScope
import dev.datlag.mimasu.LocalHaze
import dev.datlag.mimasu.tmdb.TMDB
import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.ui.navigation.DialogComponent
import dev.datlag.mimasu.ui.navigation.screen.movie.dialog.cast.CastDialogComponent
import dev.datlag.mimasu.ui.navigation.screen.movie.dialog.cast.CastDialogScreenComponent
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.flow
import org.kodein.di.DI
import org.kodein.di.instance

class MovieScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val trending: Trending.Response.Media.Movie,
    private val onBack: () -> Unit,
    private val onPlay: () -> Unit
) : MovieComponent, ComponentContext by componentContext {

    private val tmdb by instance<TMDB>()

    override val movie = flow {
        emit(tmdb.details.load(trending.id))
    }

    private val dialogNavigation = SlotNavigation<MovieDialogConfig>()
    private val dialog = childSlot(
        source = dialogNavigation,
        serializer = MovieDialogConfig.serializer(),
        childFactory = ::createDialogComponent
    )

    init {
        Napier.e { "Movie ID: ${trending.id}" }
    }

    @Composable
    override fun renderCommon() {
        onRender {
            val dialogState by dialog.subscribeAsState()

            CompositionLocalProvider(
                LocalHaze provides remember { HazeState() }
            ) {
                MovieScreen(this@MovieScreenComponent)
            }

            AnimatedVisibility(
                visible = dialogState.child != null
            ) {
                CompositionLocalProvider(
                    LocalAnimatedVisibilityScope provides this
                ) {
                    dialogState.child?.instance?.render()
                }
            }
        }
    }

    private fun createDialogComponent(
        config: MovieDialogConfig,
        context: ComponentContext
    ): DialogComponent = when (config) {
        is MovieDialogConfig.Cast -> CastDialogScreenComponent(
            componentContext = context,
            cast = config.cast,
            di = di,
            onDismiss = dialogNavigation::dismiss
        )
    }

    override fun back() {
        onBack()
    }

    override fun play() {
        onPlay()
    }

    override fun cast(value: Details.Movie.Credits.Cast) {
        dialogNavigation.activate(MovieDialogConfig.Cast(value))
    }
}