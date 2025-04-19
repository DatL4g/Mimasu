package dev.datlag.mimasu.ui.navigation.search

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.search
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.detail.movie.MovieDetail
import dev.datlag.mimasu.ui.navigation.detail.person.PersonDetail
import dev.datlag.mimasu.ui.navigation.rememberListDetailController
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
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
    val controller = rememberListDetailController<Any, Navigation.Search.Detail, Navigation.Search.Extra>()
    val detailNavigation by controller.detailValue.collectAsStateWithLifecycle()
    val extraNavigation by controller.extraValue.collectAsStateWithLifecycle()

    ListDetailPaneScaffold(
        directive = controller.scaffoldDirective,
        value = controller.scaffoldValue,
        listPane = {
            Search(
                onMovieClicked = {
                    MovieViewModel.updateFrom(it)

                    controller.navigateToDetail(Navigation.Search.Detail.Movie)
                }
            )
        },
        detailPane = {
            when (detailNavigation) {
                is Navigation.Search.Detail.Movie -> {
                    MovieDetail(
                        onBack = {
                            controller.navigateBack()
                        },
                        onCastClick = {
                            PersonViewModel.updateFrom(it)

                            controller.navigateToExtra(Navigation.Search.Extra.Person)
                        }
                    )
                }
                is Navigation.Search.Detail.Person -> {
                    PersonDetail(
                        onBack = {
                            controller.navigateBack()
                        }
                    )
                }
                else -> controller.navigateBack()
            }
        },
        extraPane = when (extraNavigation) {
            is Navigation.Search.Extra.Person -> {
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