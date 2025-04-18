package dev.datlag.mimasu.ui.navigation.search

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
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
import dev.datlag.mimasu.composeapp.generated.resources.search
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MaterialSymbols.invoke
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.ui.navigation.detail.person.PersonDetail
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

fun NavigationSuiteScope.searchItem(
    selected: Boolean,
    onClick: () -> Unit
) = item(
    selected = selected,
    onClick = onClick,
    icon = {
        MaterialSymbols(
            name = MaterialSymbols.SEARCH,
            contentDescription = null,
            filled = selected
        )
    },
    label = {
        Text(
            text = stringResource(Res.string.search),
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
    }
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SearchNavigation() {
    val navigator = rememberListDetailPaneScaffoldNavigator()
    var detailNavigation by remember { mutableStateOf<Navigation.Search.Detail>(Navigation.Search.Detail.None) }
    var extraNavigation by remember { mutableStateOf<Navigation.Search.Extra>(Navigation.Search.Extra.None) }
    val scope = rememberCoroutineScope()

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            Search(
                onMovieClicked = {
                    MovieViewModel.updateFrom(it)

                    detailNavigation = Navigation.Search.Detail.Movie
                    scope.launch {
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                }
            )
        },
        detailPane = {
            when (detailNavigation) {
                is Navigation.Search.Detail.Movie -> {
                    MovieDetail(
                        onBack = {
                            detailNavigation = Navigation.Search.Detail.None
                            scope.launch {
                                navigator.navigateBack()
                            }
                        },
                        onCastClick = {
                            PersonViewModel.updateFrom(it)

                            extraNavigation = Navigation.Search.Extra.Person
                            scope.launch {
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Extra)
                            }
                        }
                    )
                }
                is Navigation.Search.Detail.Person -> {
                    PersonDetail(
                        onBack = {
                            detailNavigation = Navigation.Search.Detail.None
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
        extraPane = if (extraNavigation is Navigation.Search.Extra.None) {
            null
        } else {
            {
                PersonDetail(
                    onBack = {
                        detailNavigation = Navigation.Search.Detail.None
                        scope.launch {
                            navigator.navigateBack()
                        }
                    }
                )
            }
        }
    )
}