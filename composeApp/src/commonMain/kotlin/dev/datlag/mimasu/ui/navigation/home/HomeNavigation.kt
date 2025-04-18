package dev.datlag.mimasu.ui.navigation.home

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.home
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MaterialSymbols.invoke
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.ui.navigation.detail.person.PersonDetail
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

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
fun HomeNavigation() {
    val navigator = rememberListDetailPaneScaffoldNavigator()
    var detailNavigation by remember { mutableStateOf<Navigation.Home.Detail>(Navigation.Home.Detail.None) }
    var extraNavigation by remember { mutableStateOf<Navigation.Home.Extra>(Navigation.Home.Extra.None) }
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
                            PersonViewModel.updateFrom(it)

                            extraNavigation = Navigation.Home.Extra.Person
                            scope.launch {
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Extra)
                            }
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
        },
        extraPane = if (extraNavigation is Navigation.Home.Extra.None) {
            null
        } else {
            {
                PersonDetail(
                    onBack = {
                        extraNavigation = Navigation.Home.Extra.None

                        scope.launch {
                            navigator.navigateBack()
                        }
                    }
                )
            }
        }
    )
}