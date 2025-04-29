package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowPosterContent

@Composable
fun ShowContent(
    hazeState: HazeState,
    listState: LazyListState,
    show: Show,
    initial: TV?,
    padding: PaddingValues,

) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .hazeSource(state = hazeState),
        contentPadding = padding
    ) {
        item {
            ShowPosterContent(
                show = show,
                initial = initial,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}