package dev.datlag.mimasu.ui.navigation.home

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.home
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.ui.navigation.detail.person.PersonDetail
import dev.datlag.mimasu.ui.navigation.detail.show.ShowDetail
import dev.datlag.mimasu.ui.navigation.rememberListDetailController
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import io.tolgee.stringResource

fun NavigationSuiteScope.homeItem(
    selected: Boolean,
    onClick: () -> Unit
) = item(
    selected = selected,
    onClick = onClick,
    icon = {
        MaterialSymbols(
            name = MaterialSymbols.HOME,
            contentDescription = null,
            filled = selected
        )
    },
    label = {
        Text(
            text = stringResource(Res.string.home),
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
    }
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun HomeNavigation(
    navigateToVideo: (VideoViewModel.WatchType) -> Unit,
    onLogin: () -> Unit,
    navigateToDiscoverTV: (Int) -> Unit,
) {
    val controller = rememberListDetailController<Any, Navigation.Home.Detail, Navigation.Home.Extra>()
    val detailNavigation by controller.detailValue.collectAsStateWithLifecycle()
    val extraNavigation by controller.extraValue.collectAsStateWithLifecycle()

    ListDetailPaneScaffold(
        directive = controller.scaffoldDirective,
        value = controller.scaffoldValue,
        listPane = {
            Home(
                onPersonClicked = {
                    PersonViewModel.updateFrom(it)

                    controller.navigateToDetail(Navigation.Home.Detail.Person)
                },
                onShowClicked = {
                    ShowViewModel.updateFrom(it)

                    controller.navigateToDetail(Navigation.Home.Detail.Show)
                },
                onMovieClicked = {
                    MovieViewModel.updateFrom(it)

                    controller.navigateToDetail(Navigation.Home.Detail.Movie)
                },
                onLogout = onLogin
            )
        },
        detailPane = {
            when (detailNavigation) {
                is Navigation.Home.Detail.Movie -> {
                    AnimatedPane {
                        MovieDetail(
                            onBack = {
                                controller.navigateBack()
                            },
                            onCastClick = {
                                PersonViewModel.updateFrom(it)

                                controller.navigateToExtra(Navigation.Home.Extra.Person)
                            },
                            onLogin = onLogin
                        )
                    }
                }
                is Navigation.Home.Detail.Person -> {
                    AnimatedPane {
                        PersonDetail(
                            onBack = {
                                controller.navigateBack()
                            }
                        )
                    }
                }
                is Navigation.Home.Detail.Show -> {
                    AnimatedPane {
                        ShowDetail(
                            onBack = {
                                controller.navigateBack()
                            },
                            onStream = navigateToVideo,
                            onDiscover = navigateToDiscoverTV,
                            onLogin = onLogin
                        )
                    }
                }
                else -> controller.navigateBack()
            }
        },
        extraPane = when (extraNavigation) {
            is Navigation.Home.Extra.Person -> {
                {
                    PersonDetail(
                        onBack = {
                            controller.navigateBack()
                        }
                    )
                }
            }
            else -> null
        }
    )
}