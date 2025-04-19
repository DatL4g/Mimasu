package dev.datlag.mimasu.ui.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class ListDetailController<ContentKey : Any, Detail : Any, Extra : Any> internal constructor(
    val navigator: ThreePaneScaffoldNavigator<ContentKey>,
    val scope: CoroutineScope,
    val listRole: ThreePaneScaffoldRole,
    val detailRole: ThreePaneScaffoldRole,
    val extraRole: ThreePaneScaffoldRole,
    private val detailState: MutableStateFlow<Detail?>,
    private val extraState: MutableStateFlow<Extra?>
) {
    val scaffoldDirective: PaneScaffoldDirective = navigator.scaffoldDirective
    val scaffoldValue: ThreePaneScaffoldValue = navigator.scaffoldValue

    val listShown: Boolean
        get() = scaffoldValue[listRole] == PaneAdaptedValue.Expanded

    val listHidden: Boolean
        get() = scaffoldValue[listRole] == PaneAdaptedValue.Hidden

    val detailShown: Boolean
        get() = scaffoldValue[detailRole] == PaneAdaptedValue.Expanded

    val detailHidden: Boolean
        get() = scaffoldValue[detailRole] == PaneAdaptedValue.Hidden

    val extraShown: Boolean
        get() = scaffoldValue[extraRole] == PaneAdaptedValue.Expanded

    val extraHidden: Boolean
        get() = scaffoldValue[extraRole] == PaneAdaptedValue.Hidden

    val detailValue = detailState.asStateFlow()
    val extraValue = extraState.asStateFlow()

    fun navigateToList() {
        detailState.update { null }
        extraState.update { null }

        if (!listShown || listHidden) {
            if (navigator.canNavigateBack()) {
                scope.launch {
                    navigator.navigateBack()

                    navigateToList()
                }
            } else {
                scope.launch {
                    navigator.navigateTo(listRole)
                }
            }
        }
    }

    fun navigateToDetail(value: Detail? = detailValue.value) {
        detailState.update { value }
        extraState.update { null }

        if (!detailShown || detailHidden) {
            if (extraShown && navigator.canNavigateBack()) {
                scope.launch {
                    navigator.navigateBack()

                    navigateToDetail(value)
                }
            } else {
                scope.launch {
                    navigator.navigateTo(detailRole)
                }
            }
        }
    }

    fun navigateToExtra(value: Extra? = extraValue.value) {
        extraState.update { value }

        if (!extraShown || extraHidden) {
            scope.launch {
                navigator.navigateTo(extraRole)
            }
        }
    }

    fun navigateBack() {
        if (navigator.canNavigateBack()) {
            scope.launch {
                navigator.navigateBack()
            }
        } else {
            when {
                extraShown || !extraHidden -> navigateToDetail()
                detailShown || !detailHidden -> navigateToList()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun <ContentKey : Any, Detail : Any, Extra : Any> rememberListDetailController(
    navigator: ThreePaneScaffoldNavigator<ContentKey> = rememberListDetailPaneScaffoldNavigator<ContentKey>(),
    scope: CoroutineScope = rememberCoroutineScope(),
    listRole: ThreePaneScaffoldRole = ListDetailPaneScaffoldRole.List,
    detailRole: ThreePaneScaffoldRole = ListDetailPaneScaffoldRole.Detail,
    extraRole: ThreePaneScaffoldRole = ListDetailPaneScaffoldRole.Extra,
    detailState: MutableStateFlow<Detail?> = remember { MutableStateFlow<Detail?>(null) },
    extraState: MutableStateFlow<Extra?> = remember { MutableStateFlow<Extra?>(null) }
) = ListDetailController(
    navigator = navigator,
    scope = scope,
    listRole = listRole,
    detailRole = detailRole,
    extraRole = extraRole,
    detailState = detailState,
    extraState = extraState
)