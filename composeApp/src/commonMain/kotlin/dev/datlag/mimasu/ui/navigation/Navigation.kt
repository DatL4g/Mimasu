package dev.datlag.mimasu.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.PaneScaffoldValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.home
import dev.datlag.mimasu.composeapp.generated.resources.movies
import dev.datlag.mimasu.composeapp.generated.resources.profile
import dev.datlag.mimasu.composeapp.generated.resources.search
import dev.datlag.mimasu.composeapp.generated.resources.series
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.ui.navigation.detail.person.PersonDetail
import dev.datlag.mimasu.ui.navigation.home.Home
import dev.datlag.mimasu.ui.navigation.home.HomeNavigation
import dev.datlag.mimasu.ui.navigation.home.homeItem
import dev.datlag.mimasu.ui.navigation.login.Login
import dev.datlag.mimasu.ui.navigation.movies.Movies
import dev.datlag.mimasu.ui.navigation.movies.MoviesNavigation
import dev.datlag.mimasu.ui.navigation.movies.movieItem
import dev.datlag.mimasu.ui.navigation.profile.Profile
import dev.datlag.mimasu.ui.navigation.profile.profileItem
import dev.datlag.mimasu.ui.navigation.search.Search
import dev.datlag.mimasu.ui.navigation.search.SearchNavigation
import dev.datlag.mimasu.ui.navigation.search.searchItem
import dev.datlag.mimasu.ui.navigation.series.Series
import dev.datlag.mimasu.ui.navigation.series.SeriesNavigation
import dev.datlag.mimasu.ui.navigation.series.seriesItem
import dev.datlag.mimasu.ui.viewmodel.AccountViewModel
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

object Navigation {

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
        }

        @Serializable
        sealed interface Extra {

            @Serializable
            data object Person : Extra
        }
    }

    @Serializable
    data object Series

    @Serializable
    data object Search {

        @Serializable
        sealed interface Detail {

            @Serializable
            data object Movie : Detail

            @Serializable
            data object Person : Detail
        }

        @Serializable
        sealed interface Extra {

            @Serializable
            data object Person : Extra
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Navigation(
    isLoggedIn: Boolean,
    loginContent: @Composable () -> Unit
) {
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsStateWithLifecycle()
    var loggedIn by remember(isLoggedIn, user) { mutableStateOf(isLoggedIn || user != null) }

    if (!loggedIn) {
        loginContent()
    } else {
        val controller = rememberNavController()
        val backStack by controller.currentBackStackEntryAsState()

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
                startDestination = Navigation.Home
            ) {
                composable<Navigation.Profile> {
                    Profile(
                        onLogout = {
                            loggedIn = user != null
                        }
                    )
                }
                composable<Navigation.Movies> {
                    MoviesNavigation()
                }
                composable<Navigation.Home> {
                    HomeNavigation()
                }
                composable<Navigation.Series> {
                    SeriesNavigation()
                }
                composable<Navigation.Search> {
                    SearchNavigation()
                }
            }
        }
    }
}