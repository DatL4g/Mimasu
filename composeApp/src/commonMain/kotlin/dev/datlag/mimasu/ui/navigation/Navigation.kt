package dev.datlag.mimasu.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.home.Home
import kotlinx.serialization.Serializable
import mimasu.composeapp.generated.resources.Res
import mimasu.composeapp.generated.resources.home
import mimasu.composeapp.generated.resources.movies
import mimasu.composeapp.generated.resources.profile
import mimasu.composeapp.generated.resources.search
import mimasu.composeapp.generated.resources.series
import org.jetbrains.compose.resources.stringResource

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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Navigation() {
    val controller = rememberNavController()
    val backStack by controller.currentBackStackEntryAsState()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            val isProfile = backStack?.destination?.hasRoute<Navigation.Profile>() ?: false
            val isMovies = backStack?.destination?.hasRoute<Navigation.Movies>() ?: false
            val isHome = backStack?.destination?.hasRoute<Navigation.Home>() ?: false
            val isSeries = backStack?.destination?.hasRoute<Navigation.Series>() ?: false
            val isSearch = backStack?.destination?.hasRoute<Navigation.Search>() ?: false

            item(
                selected = isProfile,
                onClick = {
                    controller.navigate(Navigation.Profile) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    MaterialSymbols(
                        name = MaterialSymbols.PERSON_PIN_CIRCLE,
                        contentDescription = null,
                        filled = isProfile
                    )
                },
                label = {
                    Text(text = stringResource(Res.string.profile))
                }
            )
            item(
                selected = isMovies,
                onClick = {
                    controller.navigate(Navigation.Movies) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    MaterialSymbols(
                        name = MaterialSymbols.MOVIE,
                        contentDescription = null,
                        filled = isMovies
                    )
                },
                label = {
                    Text(text = stringResource(Res.string.movies))
                }
            )
            item(
                selected = isHome,
                onClick = {
                    controller.navigate(Navigation.Home) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    MaterialSymbols(
                        name = MaterialSymbols.HOME,
                        contentDescription = null,
                        filled = isHome
                    )
                },
                label = {
                    Text(text = stringResource(Res.string.home))
                }
            )
            item(
                selected = isSeries,
                onClick = {
                    controller.navigate(Navigation.Series) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    MaterialSymbols(
                        name = MaterialSymbols.TV,
                        contentDescription = null,
                        filled = isSeries
                    )
                },
                label = {
                    Text(text = stringResource(Res.string.series))
                }
            )
            item(
                selected = isSearch,
                onClick = {
                    controller.navigate(Navigation.Search) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    MaterialSymbols(
                        name = MaterialSymbols.SEARCH,
                        contentDescription = null,
                        filled = isSearch
                    )
                },
                label = {
                    Text(text = stringResource(Res.string.search))
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
                val navigator = rememberListDetailPaneScaffoldNavigator()

                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        Home()
                    },
                    detailPane = {
                        Column {
                            Text(text = "Detail Home")
                            Button(
                                onClick = {
                                    navigator.navigateBack()
                                }
                            ) {
                                Text(text = "Back")
                            }
                        }
                    }
                )
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