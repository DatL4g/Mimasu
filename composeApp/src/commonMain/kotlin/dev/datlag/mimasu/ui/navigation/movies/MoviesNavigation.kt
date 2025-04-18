package dev.datlag.mimasu.ui.navigation.movies

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.ui.navigation.detail.person.PersonDetail
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.ui.text.style.TextOverflow
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movies
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MaterialSymbols.invoke
import org.jetbrains.compose.resources.stringResource

fun NavigationSuiteScope.movieItem(
    selected: Boolean,
    onClick: () -> Unit
) = item(
    selected = selected,
    onClick = onClick,
    icon = {
        MaterialSymbols(
            name = MaterialSymbols.MOVIE,
            contentDescription = null,
            filled = selected
        )
    },
    label = {
        Text(
            text = stringResource(Res.string.movies),
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
    }
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MoviesNavigation() {
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