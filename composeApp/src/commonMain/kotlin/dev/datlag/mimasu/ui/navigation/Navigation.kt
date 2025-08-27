package dev.datlag.mimasu.ui.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import dev.datlag.mimasu.common.dialogProperties
import dev.datlag.mimasu.ui.common.bringToFront
import dev.datlag.mimasu.ui.navigation.home.HomeNavigation
import dev.datlag.mimasu.ui.navigation.home.homeItem
import dev.datlag.mimasu.ui.navigation.login.Login
import dev.datlag.mimasu.ui.navigation.movies.MoviesNavigation
import dev.datlag.mimasu.ui.navigation.movies.movieItem
import dev.datlag.mimasu.ui.navigation.profile.Profile
import dev.datlag.mimasu.ui.navigation.profile.profileItem
import dev.datlag.mimasu.ui.navigation.search.SearchNavigation
import dev.datlag.mimasu.ui.navigation.search.searchItem
import dev.datlag.mimasu.ui.navigation.series.SeriesNavigation
import dev.datlag.mimasu.ui.navigation.series.seriesItem
import dev.datlag.mimasu.ui.navigation.video.VideoScreen
import dev.datlag.mimasu.ui.viewmodel.DiscoverViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.MainThread
import kotlinx.serialization.Serializable
import kotlin.js.JsName

@JsName("NavigationRoute")
object Navigation {

    @Serializable
    data object Login

    @Serializable
    data object Profile

    @Serializable
    data object Movies {

        @Serializable
        sealed interface Detail {

            @Serializable
            data object Movie : Detail
        }

        @Serializable
        sealed interface Extra {

            @Serializable
            data object Person : Extra
        }
    }

    @Serializable
    data object Home {

        @Serializable
        sealed interface Detail {

            @Serializable
            data object Movie : Detail

            @Serializable
            data object Person : Detail

            @Serializable
            data object Show : Detail
        }

        @Serializable
        sealed interface Extra {

            @Serializable
            data object Person : Extra
        }
    }

    @Serializable
    data object Series {


        @Serializable
        sealed interface Detail {

            @Serializable
            data object Show : Detail
        }
    }

    @Serializable
    data object Search {

        @Serializable
        sealed interface Detail {

            @Serializable
            data object Movie : Detail

            @Serializable
            data object Person : Detail

            @Serializable
            data object Show : Detail
        }

        @Serializable
        sealed interface Extra {

            @Serializable
            data object Person : Extra
        }
    }

    @Serializable
    data object Video
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, MainThread::class)
@Composable
fun Navigation() {
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsStateWithLifecycle()

    val controller = rememberNavController()
    val backStack by controller.currentBackStackEntryAsState()
    val videoNavigationController = rememberVideoNavigationController()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            val isProfile = backStack?.destination?.hasRoute<Navigation.Profile>() ?: false
            val isMovies = backStack?.destination?.hasRoute<Navigation.Movies>() ?: false
            val isHome = backStack?.destination?.hasRoute<Navigation.Home>() ?: false
            val isSeries = backStack?.destination?.hasRoute<Navigation.Series>() ?: false
            val isSearch = backStack?.destination?.hasRoute<Navigation.Search>() ?: false

            profileItem(
                selected = isProfile,
                user = user,
                onClick = {
                    if (user == null) {
                        controller.bringToFront(Navigation.Login) {
                            launchSingleTop = true
                        }
                    } else {
                        controller.bringToFront(Navigation.Profile) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
            movieItem(
                selected = isMovies,
                onClick = {
                    controller.bringToFront(Navigation.Movies) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            homeItem(
                selected = isHome,
                onClick = {
                    controller.bringToFront(Navigation.Home) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            seriesItem(
                selected = isSeries,
                onClick = {
                    controller.bringToFront(Navigation.Series) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            searchItem(
                selected = isSearch,
                onClick = {
                    controller.bringToFront(Navigation.Search) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = controller,
            startDestination = Navigation.Home
        ) {
            dialog<Navigation.Login>(
                dialogProperties = Navigation.Login.dialogProperties()
            ) {
                Login {
                    controller.bringToFront(Navigation.Home) {
                        launchSingleTop = true
                    }
                }
            }
            composable<Navigation.Profile> {
                Profile(
                    onLogout = {
                        controller.bringToFront(Navigation.Login) {
                            popUpTo(Navigation.Home)
                        }
                    }
                )
            }
            composable<Navigation.Movies> {
                MoviesNavigation(
                    onLogin = {
                        controller.bringToFront(Navigation.Login) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Home> {
                HomeNavigation(
                    navigateToVideo = {
                        videoNavigationController.loadSources(
                            data = it,
                            navigate = {
                                controller.bringToFront(Navigation.Video) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    },
                    onLogin = {
                        controller.bringToFront(Navigation.Login) {
                            popUpTo(Navigation.Home)
                        }
                    },
                    navigateToDiscoverTV = {
                        DiscoverViewModel.updateTVGenre(it)

                        controller.bringToFront(Navigation.Search) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Series> {
                SeriesNavigation(
                    navigateToVideo = {
                        videoNavigationController.loadSources(
                            data = it,
                            navigate = {
                                controller.bringToFront(Navigation.Video) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    },
                    navigateToDiscover = {
                        DiscoverViewModel.updateTVGenre(it)

                        controller.bringToFront(Navigation.Search) {
                            launchSingleTop = true
                        }
                    },
                    onLogin = {
                        controller.bringToFront(Navigation.Login) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Search> {
                SearchNavigation(
                    navigateToVideo = {
                        videoNavigationController.loadSources(
                            data = it,
                            navigate = {
                                controller.bringToFront(Navigation.Video) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    },
                    onLogin = {
                        controller.bringToFront(Navigation.Login) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            dialog<Navigation.Video>(
                dialogProperties = Navigation.Video.dialogProperties()
            ) {
                VideoScreen(
                    onBack = {
                        controller.popBackStack()
                    }
                )
            }
        }
    }
}