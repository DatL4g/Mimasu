package dev.datlag.mimasu.tv.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_tab_home
import dev.datlag.mimasu.tv.tv_tab_movies
import dev.datlag.mimasu.tv.tv_tab_profile
import dev.datlag.mimasu.tv.tv_tab_search
import dev.datlag.mimasu.tv.tv_tab_shows
import dev.datlag.mimasu.tv.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.tv.ui.navigation.detail.show.ShowDetail
import dev.datlag.mimasu.tv.ui.navigation.home.Home
import dev.datlag.mimasu.tv.ui.navigation.login.Login
import dev.datlag.mimasu.tv.ui.navigation.movies.Movies
import dev.datlag.mimasu.tv.ui.navigation.search.Search
import dev.datlag.mimasu.tv.ui.navigation.series.Series
import dev.datlag.mimasu.tv.ui.navigation.video.Video
import dev.datlag.mimasu.ui.common.bringToFront
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import io.tolgee.stringResource
import kotlinx.serialization.Serializable

object Navigation {

    @Serializable
    data object Login

    @Serializable
    data object Search

    @Serializable
    data object Home

    @Serializable
    data object Movies

    @Serializable
    data object Shows

    @Serializable
    data object Video

    object Detail {

        @Serializable
        data object Movie

        @Serializable
        data object Show
    }
}

@OptIn(MainThread::class)
@Composable
internal fun Navigation(appImage: Painter) {
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsState()

    val controller = rememberNavController()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        var tabBarHeight by remember { mutableStateOf(0.dp) }
        val navHostFocus = remember { FocusRequester() }

        NavHost(
            navController = controller,
            startDestination = Navigation.Home,
            modifier = Modifier.focusRequester(navHostFocus)
        ) {
            dialog<Navigation.Login>(
                dialogProperties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                Login(
                    appImage = appImage,
                    onSuccess = {
                        controller.bringToFront(Navigation.Home) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Search> {
                Search(
                    paddingValues = PaddingValues(top = tabBarHeight),
                    onMovieClicked = {
                        MovieViewModel.updateFrom(it)

                        controller.bringToFront(Navigation.Detail.Movie) {
                            launchSingleTop = true
                        }
                    },
                    onShowClicked = {
                        ShowViewModel.updateFrom(it)

                        controller.bringToFront(Navigation.Detail.Show) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Home> {
                Home(
                    paddingValues = PaddingValues(top = tabBarHeight),
                    onMovieClicked = {
                        MovieViewModel.updateFrom(it)

                        controller.bringToFront(Navigation.Detail.Movie) {
                            launchSingleTop = true
                        }
                    },
                    onShowClicked = {
                        ShowViewModel.updateFrom(it)

                        controller.bringToFront(Navigation.Detail.Show) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Movies> {
                Movies(
                    paddingValues = PaddingValues(top = tabBarHeight),
                    onMovieClicked = {
                        MovieViewModel.updateFrom(it)

                        controller.bringToFront(Navigation.Detail.Movie) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Shows> {
                Series(
                    paddingValues = PaddingValues(top = tabBarHeight),
                    onShowClicked = {
                        ShowViewModel.updateFrom(it)

                        controller.bringToFront(Navigation.Detail.Show) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Detail.Movie> {
                MovieDetail(
                    onLogin = {
                        controller.bringToFront(Navigation.Login) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<Navigation.Detail.Show> {
                ShowDetail(
                    onStream = {
                        if (VideoViewModel.watch(it)) {
                            controller.bringToFront(Navigation.Video) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onLogin = {
                        controller.bringToFront(Navigation.Login) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            dialog<Navigation.Video>(
                dialogProperties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                Video()
            }
        }

        LaunchedMain(user) {
            if (user == null) {
                controller.bringToFront(Navigation.Login) {
                    launchSingleTop = true
                }
            }
        }

        if (user != null) {
            TabBar(
                navController = controller,
                downFocus = navHostFocus,
                modifier = Modifier.align(Alignment.TopCenter),
                onHeightMeasured = { tabBarHeight = it }
            )
        }
    }
}

@OptIn(MainThread::class)
@Composable
private fun TabBar(
    navController: NavController,
    downFocus: FocusRequester,
    modifier: Modifier = Modifier,
    onHeightMeasured: (Dp) -> Unit
) {
    val backStack by navController.currentBackStackEntryAsState()
    val isMovieDetail = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Detail.Movie>() ?: false }
    val isShowDetail = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Detail.Show>() ?: false }
    val isVideo = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Video>() ?: false }

    if (!isMovieDetail && !isShowDetail && !isVideo) {
        val isSearch = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Search>() ?: false }
        val isHome = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Home>() ?: false }
        val isMovies = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Movies>() ?: false }
        val isShows = remember(backStack) { backStack?.destination?.hasRoute<Navigation.Shows>() ?: false }

        val searchFocus = remember { FocusRequester() }
        val homeFocus = remember { FocusRequester() }
        val moviesFocus = remember { FocusRequester() }
        val showsFocus = remember { FocusRequester() }

        val density = LocalDensity.current

        LaunchedMain(Unit) {
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
                isSearch -> 1
                isHome -> 2
                isMovies -> 3
                isShows -> 4
                else -> 2
            },
            modifier = modifier.focusRestorer().padding(16.dp).onGloballyPositioned { coordinates ->
                with(density) {
                    onHeightMeasured(coordinates.size.height.toDp())
                }
            }
        ) {
            val focusManager = LocalFocusManager.current

            Tab(
                modifier = Modifier
                    .focusProperties {
                        down = downFocus
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                selected = false,
                onFocus = {
                    focusManager.moveFocus(FocusDirection.Right)
                },
                enabled = false
            ) {
                val accountViewModel = accountViewModel()
                val user by accountViewModel.user.collectAsState()

                AsyncImage(
                    modifier = Modifier.size(ButtonDefaults.IconSize).clip(CircleShape),
                    model = user?.profilePictures?.firstOrNull(),
                    contentScale = ContentScale.Crop,
                    error = rememberNestedImagePainter(
                        models = user?.profilePictures?.drop(1).orEmpty(),
                        contentScale = ContentScale.Crop,
                        error = MaterialSymbols.rememberPainter(name = MaterialSymbols.ACCOUNT_CIRCLE)
                    ),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = user?.name ?: stringResource(Res.string.tv_tab_profile))
            }
            Tab(
                modifier = Modifier
                    .focusRequester(searchFocus)
                    .focusProperties {
                        down = downFocus
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                selected = isSearch,
                onFocus = {
                    if (!isSearch) {
                        navController.bringToFront(Navigation.Search) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
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
                    .focusProperties {
                        down = downFocus
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                selected = isHome,
                onFocus = {
                    if (!isHome) {
                        navController.bringToFront(Navigation.Home) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
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
                    .focusProperties {
                        down = downFocus
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                selected = isMovies,
                onFocus = {
                    if (!isMovies) {
                        navController.bringToFront(Navigation.Movies) {
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
                    .focusProperties {
                        down = downFocus
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                selected = isShows,
                onFocus = {
                    if (!isShows) {
                        navController.bringToFront(Navigation.Shows) {
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
}