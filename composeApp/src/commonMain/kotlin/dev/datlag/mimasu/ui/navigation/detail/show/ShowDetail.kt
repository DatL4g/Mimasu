package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowToolbar
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShowDetail(
    onBack: () -> Unit,
) {
    val showViewModel = kodeinViewModel<ShowViewModel>()
    val showState by showViewModel.show.collectAsStateWithLifecycle(ShowViewModel.State.Loading)
    val initial by showViewModel.initialShow.collectAsStateWithLifecycle()

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val haze = remember { HazeState() }
    val listState = rememberLazyListState()

    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ShowToolbar(
                appBarState = appBarState,
                scrollBehavior = scrollBehavior,
                hazeState = haze,
                listState = listState,
                show = showState.getOrNull(),
                initial = initial,
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
            )
        }
    ) { padding ->
        when (val current = showState) {
            is ShowViewModel.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.5F).clip(CircleShape)
                    )
                }
            }
            is ShowViewModel.State.Error -> {
                Box(
                    modifier = Modifier.padding(padding)
                ) {
                    Text(text = "Loading Show failed: ${current.throwable}")
                }
            }
            is ShowViewModel.State.Success -> ShowContent(
                hazeState = haze,
                listState = listState,
                show = current.show,
                initial = initial,
                padding = padding,
            )
        }
    }
}