package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography

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
        modifier = modifier.height(192.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            val backdrops = remember(show?.id) { show.backdrops(fallback = null) }
            val background = Platform.colorScheme().background.copy(alpha = 0.7F)
            val hazeStyle = remember(background) {
                HazeStyle(
                    backgroundColor = background,
                    blurRadius = 5.dp,
                    tint = HazeTint(background)
                )
            }

            AsyncImage(
                modifier = Modifier
                    .hazeEffect(hazeStyle)
                    .fillMaxSize(),
                model = backdrops.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = backdrops.drop(1),
                    contentScale = ContentScale.Crop
                )
            )
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val posters = remember(show?.id) { show.posters(fallbackShow = null) }
                var loading by remember(show?.id) { mutableStateOf(true) }

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
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = show?.name ?: show?.originalName.orEmpty(),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        style = Platform.typography().titleLarge,
                        color = Platform.colorScheme().onBackground
                    )
                    Text(
                        text = show?.overview ?: show?.tagline.orEmpty(),
                        color = Platform.colorScheme().onBackground,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}