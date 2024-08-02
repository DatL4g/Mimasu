package dev.datlag.mimasu.ui.navigation.screen.initial

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import dev.datlag.mimasu.tv.TvInitial
import dev.datlag.mimasu.tv.TvInitialNavigation
import dev.datlag.mimasu.tv.TvInitialSearch
import dev.datlag.mimasu.ui.navigation.Component
import dev.datlag.mimasu.ui.navigation.screen.initial.home.HomeScreenComponent
import org.kodein.di.DI

class InitialScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
) : InitialComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<InitialConfig>()
    private val stack = childStack(
        source = navigation,
        serializer = InitialConfig.serializer(),
        initialConfiguration = InitialConfig.Home,
        handleBackButton = true,
        childFactory = ::createChildComponent
    )

    private fun createChildComponent(
        config: InitialConfig,
        componentContext: ComponentContext
    ): Component = when (config) {
        is InitialConfig.Home -> HomeScreenComponent(
            componentContext = componentContext,
            di = di
        )
    }

    @Composable
    override fun renderCommon() {
        onRender {
            CommonInitialScreen {
                content()
            }
        }
    }

    @Composable
    override fun renderTv() {
        onRender {
            var searchQuery by remember { mutableStateOf("") }

            TvInitial(
                profile = TvInitialNavigation(
                    selected = false,
                    icon = Icons.Rounded.AccountCircle,
                    label = "Profile",
                    onClick = { }
                ),
                home = TvInitialNavigation(
                    selected = true,
                    icon = Icons.Rounded.Home,
                    label = "Home",
                    onClick = { }
                ),
                favorites = TvInitialNavigation(
                    selected = false,
                    icon = Icons.Rounded.Favorite,
                    label = "Favorites",
                    onClick = { }
                ),
                search = TvInitialSearch(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            ) {
                content()
            }
        }
    }

    @Composable
    private fun content() {
        Children(
            stack = stack,
            animation = stackAnimation(fade()),
        ) {
            it.instance.render()
        }
    }
}