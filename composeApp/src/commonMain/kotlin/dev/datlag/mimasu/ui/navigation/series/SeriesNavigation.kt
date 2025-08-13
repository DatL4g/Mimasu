package dev.datlag.mimasu.ui.navigation.series

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
import dev.datlag.mimasu.composeapp.generated.resources.series
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.mimasu.ui.navigation.detail.show.ShowDetail
import dev.datlag.mimasu.ui.navigation.rememberListDetailController
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import io.tolgee.stringResource

fun NavigationSuiteScope.seriesItem(
    selected: Boolean,
    onClick: () -> Unit
) = item(
    selected = selected,
    onClick = onClick,
    icon = {
        MaterialSymbols(
            name = MaterialSymbols.TV,
            contentDescription = null,
            filled = selected
        )
    },
    label = {
        Text(
            text = stringResource(Res.string.series),
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
    }
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SeriesNavigation(
    navigateToVideo: (VideoViewModel.WatchType) -> Unit,
    navigateToDiscover: (Int) -> Unit,
    onLogin: () -> Unit
) {
    val controller = rememberListDetailController<Any, Navigation.Series.Detail, Nothing>()
    val detailNavigation by controller.detailValue.collectAsStateWithLifecycle()

    ListDetailPaneScaffold(
        directive = controller.scaffoldDirective,
        value = controller.scaffoldValue,
        listPane = {
            Series(
                onSeriesClicked = {
                    ShowViewModel.updateFrom(it)

                    controller.navigateToDetail(Navigation.Series.Detail.Show)
                }
            )
        },
        detailPane = {
            when (detailNavigation) {
                is Navigation.Series.Detail.Show -> {
                    AnimatedPane {
                        ShowDetail(
                            onBack = {
                                controller.navigateBack()
                            },
                            onStream = navigateToVideo,
                            onDiscover = navigateToDiscover,
                            onLogin = onLogin
                        )
                    }
                }
                else -> controller.navigateBack()
            }
        }
    )
}