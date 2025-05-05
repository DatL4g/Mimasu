package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography

@Composable
fun EpisodeItem(
    episode: Season.Episode,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        onClick = { },
        colors = CardDefaults.elevatedCardColors(
            containerColor = Platform.colorScheme().background,
            contentColor = Platform.colorScheme().onBackground
        ),
        elevation = CardDefaults.elevatedCardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val posters = remember(episode.showId, episode.id) { episode.posters(fallbackShow = null) }

            AsyncImage(
                modifier = Modifier
                    .padding(8.dp)
                    .height(84.dp)
                    .aspectRatio(1.75F, true)
                    .clip(Platform.shapes().medium),
                model = posters.firstOrNull(),
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = posters.drop(1),
                    contentScale = ContentScale.Crop
                ),
                contentDescription = null
            )

            Column(
                modifier = Modifier.weight(1F).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val overview = remember(episode.showId, episode.id) { episode.overview?.ifBlank { null } }

                Text(
                    text = episode.name ?: "Episode: ${episode.episodeNumber}",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
                if (!overview.isNullOrBlank()) {
                    Text(
                        text = overview,
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        style = Platform.typography().labelMedium
                    )
                }
            }
        }
    }
}