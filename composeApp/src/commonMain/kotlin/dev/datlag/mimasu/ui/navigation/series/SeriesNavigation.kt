package dev.datlag.mimasu.ui.navigation.series

import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
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
import org.jetbrains.compose.resources.stringResource

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
fun SeriesNavigation() {
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
            AnimatedPane {
                when (detailNavigation) {
                    is Navigation.Series.Detail.Show -> {
                        ShowDetail(
                            onBack = {
                                controller.navigateBack()
                            }
                        )
                    }
                    else -> controller.navigateBack()
                }
            }
        }
    )
}