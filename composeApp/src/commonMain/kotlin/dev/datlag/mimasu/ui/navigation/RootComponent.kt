package dev.datlag.mimasu.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import dev.datlag.mimasu.common.isTv
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.other.ContentDetails
import dev.datlag.mimasu.ui.navigation.screen.initial.InitialComponent
import dev.datlag.mimasu.ui.navigation.screen.initial.InitialScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.initial.home.HomeScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.login.LoginScreen
import dev.datlag.mimasu.ui.navigation.screen.login.LoginScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.movie.MovieComponent
import dev.datlag.mimasu.ui.navigation.screen.movie.MovieScreen
import dev.datlag.mimasu.ui.navigation.screen.movie.MovieScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.video.VideoScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.video.dialog.VideoDialogScreenComponent
import dev.datlag.tooling.scopeCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.instanceOrNull
import kotlin.coroutines.cancellation.CancellationException

class RootComponent(
    componentContext: ComponentContext,
    override val di: DI
) : Component, ComponentContext by componentContext {

    private val navigation = StackNavigation<RootConfig>()
    private val stack = childStack(
        source = navigation,
        serializer = RootConfig.serializer(),
        initialConfiguration = if (isLoggedIn()) RootConfig.Initial else RootConfig.Login,
        handleBackButton = true,
        childFactory = ::createScreenComponent
    )

    private val dialogNavigation = SlotNavigation<RootDialogConfig>()
    private val dialog = childSlot(
        source = dialogNavigation,
        serializer = RootDialogConfig.serializer(),
        childFactory = ::createDialogComponent
    )

    private fun createScreenComponent(
        rootConfig: RootConfig,
        componentContext: ComponentContext
    ): Component = when (rootConfig) {
        is RootConfig.Initial -> InitialScreenComponent(
            componentContext = componentContext,
            di = di,
            visible = stack.active.instance is InitialComponent,
            onMovie = {
                navigation.pushToFront(it)
            },
            watchVideo = ::showVideo
        )
        is RootConfig.Login -> LoginScreenComponent(
            componentContext = componentContext,
            di = di,
            toHome = {
                navigation.replaceAll(RootConfig.Initial)
            }
        )
        is RootConfig.Movie -> MovieScreenComponent(
            componentContext = componentContext,
            di = di,
            trending = rootConfig.trending,
            visible = stack.active.instance is MovieComponent,
            onBack = {
                navigation.pop()
            },
            onPlay = {
                showVideo()
            }
        )
        is RootConfig.Video -> VideoScreenComponent(
            componentContext = componentContext,
            di = di
        )
    }

    private fun createDialogComponent(
        config: RootDialogConfig,
        context: ComponentContext
    ): Component = when (config) {
        is RootDialogConfig.Video -> VideoDialogScreenComponent(
            componentContext = context,
            di = di,
            onDismiss = {
                dialogNavigation.dismiss {
                    if (it) {
                        ContentDetails.setShowingPlayer(false)
                    }
                }
            }
        )
    }

    private fun showVideo() {
        if (isTv() || stack.active.configuration is RootConfig.Video) {
            navigation.bringToFront(RootConfig.Video)
        } else {
            dialogNavigation.activate(RootDialogConfig.Video) {
                ContentDetails.setShowingPlayer(true)
            }
        }
    }

    @OptIn(ExperimentalDecomposeApi::class, ExperimentalSharedTransitionApi::class)
    @Composable
    @NonRestartableComposable
    override fun renderCommon(scope: SharedTransitionScope) {
        onRender {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val dialogState by dialog.subscribeAsState()

                Children(
                    stack = stack,
                    animation = predictiveBackAnimation(
                        backHandler = this@RootComponent.backHandler,
                        fallbackAnimation = stackAnimation(fade()),
                        onBack = {
                            navigation.pop()
                        }
                    )
                ) {
                    it.instance.render(scope)
                }

                dialogState.child?.instance?.render(scope)
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        return scopeCatching {
            Firebase.auth.currentUser
        }.getOrNull() != null
    }

    private fun login() {
        navigation.replaceAll(RootConfig.Login)
    }
}