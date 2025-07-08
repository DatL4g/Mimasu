package dev.datlag.mimasu.tv.ui.custom

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.TV
import kotlinx.coroutines.flow.Flow

@Composable
internal fun SeriesSection(
    flow: Flow<PagingData<TV>>,
    title: String,
    orientation: Orientation,
    modifier: Modifier = Modifier,
    onClick: (TV) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val shows = flow.collectAsLazyPagingItems()
        val (refreshPlaceholder, appendPlaceholder) = remember(orientation) {
            when (orientation) {
                Orientation.Horizontal -> 5 to 3
                Orientation.Vertical -> 8 to 5
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp)
        ) {
            items(shows.itemCount) { index ->
                val show = shows[index]

                ShowCard(
                    tv = show,
                    orientation = orientation,
                    onClick = onClick,
                )
            }
            when {
                shows.loadState.refresh is LoadState.Loading -> {
                    items(refreshPlaceholder) {
                        ShowCard(show = null, orientation)
                    }
                }
                shows.loadState.append is LoadState.Loading -> {
                    items(appendPlaceholder) {
                        ShowCard(show = null, orientation)
                    }
                }
            }
        }
    }
}