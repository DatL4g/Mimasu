package dev.datlag.mimasu.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.common.dialogProperties
import dev.datlag.mimasu.ui.navigation.home.HomeNavigation
import dev.datlag.mimasu.ui.navigation.home.homeItem
import dev.datlag.mimasu.ui.navigation.movies.MoviesNavigation
import dev.datlag.mimasu.ui.navigation.movies.movieItem
import dev.datlag.mimasu.ui.navigation.profile.Profile
import dev.datlag.mimasu.ui.navigation.profile.profileItem
import dev.datlag.mimasu.ui.navigation.search.SearchNavigation
import dev.datlag.mimasu.ui.navigation.search.searchItem
import dev.datlag.mimasu.ui.navigation.series.SeriesNavigation
import dev.datlag.mimasu.ui.navigation.series.seriesItem
import dev.datlag.mimasu.ui.navigation.video.VideoScreen
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import kotlinx.serialization.Serializable
import dev.datlag.mimasu.ui.ads.rememberAdManager
import dev.datlag.mimasu.ui.navigation.login.Login
import dev.datlag.mimasu.ui.viewmodel.loginViewModel

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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
                    controller.navigate(Navigation.Profile) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            movieItem(
                selected = isMovies,
                onClick = {
                    controller.navigate(Navigation.Movies) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            homeItem(
                selected = isHome,
                onClick = {
                    controller.navigate(Navigation.Home) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            seriesItem(
                selected = isSeries,
                onClick = {
                    controller.navigate(Navigation.Series) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            searchItem(
                selected = isSearch,
                onClick = {
                    controller.navigate(Navigation.Search) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = controller,
            startDestination = if (user == null) {
                Navigation.Login
            } else {
                Navigation.Home
            }
        ) {
            dialog<Navigation.Login>(
                dialogProperties = Navigation.Login.dialogProperties()
            ) {
                Login {
                    controller.navigate(Navigation.Home)
                }
            }
            composable<Navigation.Profile> {
                Profile(
                    onLogout = {
                        controller.navigate(Navigation.Login) {
                            popUpTo(Navigation.Home) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<Navigation.Movies> {
                MoviesNavigation()
            }
            composable<Navigation.Home> {
                HomeNavigation(
                    navigateToVideo = {
                        videoNavigationController.loadSources(
                            data = it,
                            navigate = {
                                controller.navigate(Navigation.Video) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    },
                    onLogout = {
                        controller.navigate(Navigation.Login) {
                            popUpTo(Navigation.Home) {
                                inclusive = true
                            }
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
                                controller.navigate(Navigation.Video) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                )
            }
            composable<Navigation.Search> {
                SearchNavigation(
                    navigateToVideo = {
                        videoNavigationController.loadSources(
                            data = it,
                            navigate = {
                                controller.navigate(Navigation.Video) {
                                    launchSingleTop = true
                                }
                            }
                        )
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