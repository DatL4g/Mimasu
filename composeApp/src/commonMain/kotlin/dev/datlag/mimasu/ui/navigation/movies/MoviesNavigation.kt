package dev.datlag.mimasu.ui.navigation.movies

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
import dev.datlag.mimasu.composeapp.generated.resources.movies
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.ui.navigation.detail.person.PersonDetail
import dev.datlag.mimasu.ui.navigation.rememberListDetailController
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import io.tolgee.stringResource

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
fun MoviesNavigation(
    onLogin: () -> Unit
) {
    val controller = rememberListDetailController<Any, Navigation.Movies.Detail, Navigation.Movies.Extra>()
    val detailNavigation by controller.detailValue.collectAsStateWithLifecycle()
    val extraNavigation by controller.extraValue.collectAsStateWithLifecycle()

    // ToDo("replace with navigable when available")
    ListDetailPaneScaffold(
        directive = controller.scaffoldDirective,
        value = controller.scaffoldValue,
        listPane = {
            Movies(
                onMovieClicked = {
                    MovieViewModel.updateFrom(it)

                    controller.navigateToDetail(Navigation.Movies.Detail.Movie)
                }
            )
        },
        detailPane = {
            when (detailNavigation) {
                is Navigation.Movies.Detail.Movie -> {
                    AnimatedPane {
                        MovieDetail(
                            onBack = {
                                controller.navigateBack()
                            },
                            onCastClick = {
                                PersonViewModel.updateFrom(it)

                                controller.navigateToExtra(Navigation.Movies.Extra.Person)
                            },
                            onLogin = onLogin
                        )
                    }
                }
                else -> controller.navigateBack()
            }
        },
        extraPane = when (extraNavigation) {
            is Navigation.Movies.Extra.Person -> {
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