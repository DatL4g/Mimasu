package dev.datlag.mimasu.tv.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import dev.datlag.mimasu.tv.ui.navigation.home.Home
import kotlinx.serialization.Serializable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.TabDefaults
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_tab_home
import dev.datlag.mimasu.tv.tv_tab_movies
import dev.datlag.mimasu.tv.tv_tab_search
import dev.datlag.mimasu.tv.tv_tab_shows
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import org.jetbrains.compose.resources.stringResource

object Navigation {

    @Serializable
    data object Search

    @Serializable
    data object Home

    @Serializable
    data object Movies

    @Serializable
    data object Shows
}

@Composable
fun Navigation() {
    val controller = rememberNavController()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabBar(
            navController = controller
        )
        NavHost(
            navController = controller,
            startDestination = Navigation.Home
        ) {
            composable<Navigation.Search> {
                Text(text = "Search Screen")
            }
            composable<Navigation.Home> {
                Home()
            }
            composable<Navigation.Movies> {
                Text(text = "Movies Screen")
            }
            composable<Navigation.Shows> {
                Text(text = "Shows Screen")
            }
        }
    }
}

@Composable
private fun TabBar(navController: NavController) {
    val backStack by navController.currentBackStackEntryAsState()

    val isSearch = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Search>() ?: false }
    val isHome = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Home>() ?: false }
    val isMovies = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Movies>() ?: false }
    val isShows = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Shows>() ?: false }

    val searchFocus = remember { FocusRequester() }
    val homeFocus = remember { FocusRequester() }
    val moviesFocus = remember { FocusRequester() }
    val showsFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        when {
            isSearch -> searchFocus.requestFocus()
            isHome -> homeFocus.requestFocus()
            isMovies -> moviesFocus.requestFocus()
            isShows -> showsFocus.requestFocus()
            else -> homeFocus.requestFocus()
        }
    }

    TabRow(
        selectedTabIndex = when {
            isSearch -> 0
            isHome -> 1
            isMovies -> 2
            isShows -> 3
            else -> 1
        },
        modifier = Modifier.focusRestorer().padding(16.dp)
    ) {
        Tab(
            modifier = Modifier
                .focusRequester(searchFocus)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            selected = isSearch,
            onFocus = {
                if (!isSearch) {
                    navController.navigate(Navigation.Search) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
        ) {
            MaterialSymbols(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                name = MaterialSymbols.SEARCH,
                contentDescription = null,
                filled = isSearch
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.tv_tab_search))
        }
        Tab(
            modifier = Modifier
                .focusRequester(homeFocus)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            selected = isHome,
            onFocus = {
                if (!isHome) {
                    navController.navigate(Navigation.Home) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
        ) {
            MaterialSymbols(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                name = MaterialSymbols.HOME,
                contentDescription = null,
                filled = isHome
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.tv_tab_home))
        }
        Tab(
            modifier = Modifier
                .focusRequester(moviesFocus)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            selected = isMovies,
            onFocus = {
                if (!isMovies) {
                    navController.navigate(Navigation.Movies) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
        ) {
            MaterialSymbols(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                name = MaterialSymbols.MOVIE,
                contentDescription = null,
                filled = isMovies
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.tv_tab_movies))
        }
        Tab(
            modifier = Modifier
                .focusRequester(showsFocus)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            selected = isShows,
            onFocus = {
                if (!isShows) {
                    navController.navigate(Navigation.Shows) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
        ) {
            MaterialSymbols(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                name = MaterialSymbols.TV,
                contentDescription = null,
                filled = isShows
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.tv_tab_shows))
        }
    }
}