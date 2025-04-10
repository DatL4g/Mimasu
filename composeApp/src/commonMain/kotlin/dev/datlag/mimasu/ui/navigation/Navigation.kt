package dev.datlag.mimasu.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import dev.datlag.mimasu.ui.navigation.login.Login
import dev.datlag.mimasu.ui.navigation.movies.Movies
import dev.datlag.mimasu.ui.navigation.search.Search
import dev.datlag.mimasu.ui.navigation.series.Series
import dev.datlag.mimasu.ui.viewmodel.AccountViewModel
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
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
            data object None : Detail

            @Serializable
            data object Movie : Detail
        }

        @Serializable
        sealed interface Extra {

            @Serializable
            data object None : Extra

            @Serializable
            data object Person : Extra
        }
    }

    @Serializable
    data object Home {

        @Serializable
        sealed interface Detail {

            @Serializable
            data object None : Detail

            @Serializable
            data object Movie : Detail

            @Serializable
            data object Person : Detail
        }
    }

    @Serializable
    data object Series

    @Serializable
    data object Search
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Navigation() {
    val accountViewModel = accountViewModel()
    val controller = rememberNavController()
    val backStack by controller.currentBackStackEntryAsState()
    val user by accountViewModel.user.collectAsStateWithLifecycle()

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
                    var fallback by remember(user?.email) { mutableStateOf(false) }

                    if (fallback) {
                        MaterialSymbols(
                            name = MaterialSymbols.PERSON_PIN_CIRCLE,
                            contentDescription = null,
                            filled = isProfile
                        )
                    } else {
                        AsyncImage(
                            modifier = Modifier.size(24.dp).clip(CircleShape),
                            model = user?.profilePictures?.firstOrNull(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            error = rememberNestedImagePainter(
                                models = user?.profilePictures.orEmpty(),
                                contentScale = ContentScale.Crop
                            ),
                            onLoading = {
                                fallback = false
                            },
                            onSuccess = {
                                fallback = false
                            },
                            onError = {
                                fallback = true
                            }
                        )
                    }
                },
                label = {
                    Text(text = user?.name ?: stringResource(Res.string.profile))
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
                Login()
            }
            composable<Navigation.Movies> {
                val navigator = rememberListDetailPaneScaffoldNavigator()
                var detailNavigation by remember { mutableStateOf<Navigation.Movies.Detail>(Navigation.Movies.Detail.None) }
                var extraNavigation by remember { mutableStateOf<Navigation.Movies.Extra>(Navigation.Movies.Extra.None) }
                val scope = rememberCoroutineScope()

                // ToDo("replace with navigable when available")
                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        Movies(
                            onMovieClicked = {
                                MovieViewModel.updateFrom(it)

                                detailNavigation = Navigation.Movies.Detail.Movie
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                }
                            }
                        )
                    },
                    detailPane = {
                        when (detailNavigation) {
                            is Navigation.Movies.Detail.Movie -> {
                                MovieDetail(
                                    onBack = {
                                        detailNavigation = Navigation.Movies.Detail.None

                                        scope.launch {
                                            navigator.navigateBack()
                                        }
                                    },
                                    onCastClick = {
                                        PersonViewModel.updateFrom(it)

                                        extraNavigation = Navigation.Movies.Extra.Person
                                        scope.launch {
                                            navigator.navigateTo(ListDetailPaneScaffoldRole.Extra)
                                        }
                                    }
                                )
                            }
                            else -> {
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.List)
                                }
                            }
                        }
                    },
                    extraPane = if (extraNavigation is Navigation.Movies.Extra.None) {
                        null
                    } else {
                        {
                            PersonDetail(
                                onBack = {
                                    extraNavigation = Navigation.Movies.Extra.None

                                    scope.launch {
                                        navigator.navigateBack()
                                    }
                                }
                            )
                        }
                    }
                )
            }
            composable<Navigation.Home> {
                val navigator = rememberListDetailPaneScaffoldNavigator(
                    isDestinationHistoryAware = false
                )
                var detailNavigation by remember { mutableStateOf<Navigation.Home.Detail>(Navigation.Home.Detail.None) }
                val scope = rememberCoroutineScope()

                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        Home(
                            onPersonClicked = {
                                PersonViewModel.updateFrom(it)

                                detailNavigation = Navigation.Home.Detail.Person
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                }
                            },
                            onMovieClicked = {
                                MovieViewModel.updateFrom(it)

                                detailNavigation = Navigation.Home.Detail.Movie
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                                }
                            }
                        )
                    },
                    detailPane = {
                        when (detailNavigation) {
                            is Navigation.Home.Detail.Movie -> {
                                MovieDetail(
                                    onBack = {
                                        detailNavigation = Navigation.Home.Detail.None
                                        scope.launch {
                                            navigator.navigateBack()
                                        }
                                    },
                                    onCastClick = {

                                    }
                                )
                            }
                            is Navigation.Home.Detail.Person -> {
                                PersonDetail(
                                    onBack = {
                                        detailNavigation = Navigation.Home.Detail.None
                                        scope.launch {
                                            navigator.navigateBack()
                                        }
                                    }
                                )
                            }
                            else -> {
                                scope.launch {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.List)
                                }
                            }
                        }
                    }
                )
            }
            composable<Navigation.Series> {
                val navigator = rememberListDetailPaneScaffoldNavigator(
                    isDestinationHistoryAware = false
                )

                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        Series(
                            onSeriesClicked = {
                                // ToDo
                            }
                        )
                    },
                    detailPane = {
                        // ToDo
                    }
                )
            }
            composable<Navigation.Search> {
                Search()
            }
        }
    }
}