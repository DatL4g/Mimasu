package dev.datlag.mimasu.tv.ui.navigation.detail.show

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tv.ui.navigation.detail.show.components.ShowPosterContent

@Composable
internal fun ShowContent(
    show: Show?,
    initial: TV?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ShowPosterContent(
                show = show,
                initial = initial,
                modifier = Modifier.fillParentMaxWidth().fillParentMaxHeight(0.8F)
            )
        }
        item {
            val overview = remember(show?.overview, initial?.overview) {
                show?.overview?.ifBlank { null } ?: initial?.overview?.ifBlank { null }
            }

            overview?.let {
                Text(
                    modifier = Modifier.fillParentMaxWidth().padding(32.dp),
                    text = it,
                    softWrap = true
                )
            }
        }
    }
}