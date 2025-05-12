package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes

@Composable
fun ShowPager(
    show: Show?,
    modifier: Modifier = Modifier,
    onClick: (Show) -> Unit = { }
) {
    Card(
        onClick = {
            show?.let(onClick)
        },
        modifier = Modifier.height(200.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            val backdrops = remember(show?.id) { show.backdrops(fallback = null) }
            val posters = remember(show?.id) { show.posters(fallbackShow = null) }
            var loading by remember(show?.id) { mutableStateOf(true) }

            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = backdrops.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = backdrops.drop(1),
                    contentScale = ContentScale.Crop
                )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Platform.colorScheme().background.copy(alpha = 0.5F))
            )
            AsyncImage(
                modifier = Modifier
                    .size(width = 100.dp, height = 160.dp)
                    .clip(Platform.shapes().medium)
                    .placeholder(
                        visible = loading,
                        shape = Platform.shapes().medium,
                        highlight = PlaceholderHighlight.fade()
                    ),
                model = posters.firstOrNull(),
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = posters.drop(1),
                    contentScale = ContentScale.Crop,
                    onError = {
                        loading = true
                    },
                    onSuccess = {
                        loading = false
                    }
                ),
                contentDescription = show?.name,
                onLoading = {
                    loading = true
                },
                onSuccess = {
                    loading = false
                }
            )
        }
    }
}