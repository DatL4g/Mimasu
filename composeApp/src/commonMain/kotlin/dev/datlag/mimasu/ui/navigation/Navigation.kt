package dev.datlag.mimasu.ui.navigation

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PersonPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.ui.navigation.home.Home
import kotlinx.serialization.Serializable

object Navigation {

    @Serializable
    data object Profile

    @Serializable
    data object Movies

    @Serializable
    data object Home

    @Serializable
    data object Series

    @Serializable
    data object Search

    @Serializable
    data class Detail(val param: String)
}

@Composable
fun Navigation() {
    val controller = rememberNavController()
    val backStack by controller.currentBackStackEntryAsState()

    NavigationSuiteScaffold(
        modifier = Modifier.statusBarsPadding(),
        navigationSuiteItems = {
            item(
                selected = backStack?.destination?.hasRoute<Navigation.Profile>() ?: false,
                onClick = {
                    controller.navigate(Navigation.Profile) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.PersonPin,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Profile")
                }
            )
            item(
                selected = backStack?.destination?.hasRoute<Navigation.Movies>() ?: false,
                onClick = {
                    controller.navigate(Navigation.Movies) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Movie,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Movies")
                }
            )
            item(
                selected = backStack?.destination?.hasRoute<Navigation.Home>() ?: false,
                onClick = {
                    controller.navigate(Navigation.Home) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Home")
                }
            )
            item(
                selected = backStack?.destination?.hasRoute<Navigation.Series>() ?: false,
                onClick = {
                    controller.navigate(Navigation.Series) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Tv,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Series")
                }
            )
            item(
                selected = backStack?.destination?.hasRoute<Navigation.Search>() ?: false,
                onClick = {
                    controller.navigate(Navigation.Search) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Search")
                }
            )
        }
    ) {
        NavHost(
            navController = controller,
            startDestination = Navigation.Home
        ) {
            composable<Navigation.Profile> {
                Text(text = "Profile Screen")
            }
            composable<Navigation.Movies> {
                Text(text = "Movies Screen")
            }
            composable<Navigation.Home> {
                Home()
            }
            composable<Navigation.Series> {
                Text(text = "Series Screen")
            }
            composable<Navigation.Search> {
                Text(text = "Search Screen")
            }
            composable<Navigation.Detail> {
                val route = it.toRoute<Navigation.Detail>()
                Text(text = "Detail Screen${route.param}")
            }
        }
    }
}