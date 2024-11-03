package dev.datlag.mimasu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.router.stack.replaceAll
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.ui.navigation.screen.initial.InitialScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.initial.home.HomeScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.login.LoginScreen
import dev.datlag.mimasu.ui.navigation.screen.login.LoginScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.movie.MovieScreen
import dev.datlag.mimasu.ui.navigation.screen.movie.MovieScreenComponent
import dev.datlag.mimasu.ui.navigation.screen.video.VideoScreenComponent
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

    private fun createScreenComponent(
        rootConfig: RootConfig,
        componentContext: ComponentContext
    ): Component = when (rootConfig) {
        is RootConfig.Initial -> InitialScreenComponent(
            componentContext = componentContext,
            di = di,
            onMovie = {
                navigation.pushToFront(it)
            },
            watchVideo = {
                navigation.bringToFront(RootConfig.Video)
            }
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
            onBack = {
                navigation.pop()
            }
        )
        is RootConfig.Video -> VideoScreenComponent(
            componentContext = componentContext,
            di = di
        )
    }

    @OptIn(ExperimentalDecomposeApi::class)
    @Composable
    @NonRestartableComposable
    override fun renderCommon() {
        onRender {
            Children(
                stack = stack,
                animation = predictiveBackAnimation(
                    backHandler = this.backHandler,
                    fallbackAnimation = stackAnimation(fade()),
                    onBack = {
                        navigation.pop()
                    }
                )
            ) {
                it.instance.render()
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