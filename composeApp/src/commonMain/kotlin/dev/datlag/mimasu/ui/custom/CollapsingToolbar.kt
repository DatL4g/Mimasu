package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.mimasu.LocalHaze
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun CollapsingToolbar(
    state: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    background: @Composable BoxScope.(CollapsingToolbarState) -> Unit = { },
    navigationIcon: @Composable (CollapsingToolbarState) -> Unit = { },
    title: @Composable (CollapsingToolbarState) -> Unit,
    actions: @Composable RowScope.(CollapsingToolbarState) -> Unit = { },
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val collapseState by remember(state) {
            derivedStateOf {
                CollapsingToolbarState(
                    expandProgress = max(min(1F - state.collapsedFraction, 1F), 0F)
                )
            }
        }

        background(collapseState)
        LargeTopAppBar(
            navigationIcon = {
                navigationIcon(collapseState)
            },
            title = {
                title(collapseState)
            },
            actions = {
                actions(collapseState)
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            modifier = modifier
        )
    }
}