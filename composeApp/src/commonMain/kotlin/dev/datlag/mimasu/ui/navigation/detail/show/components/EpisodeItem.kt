package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.detail.show.rememberEpisodeStreamState
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
fun EpisodeItem(
    tmdbId: Int?,
    episode: Season.Episode,
    seasonNumber: Int?,
    showAvailability: Boolean,
    modifier: Modifier = Modifier,
    onStream: (Extension.Response) -> Unit
) {
    val episodeStreamState = if (showAvailability) {
        rememberEpisodeStreamState(
            tmdbId = tmdbId,
            seasonNumber = seasonNumber,
            episode = episode
        )
    } else {
        null
    }
    val scope = rememberCoroutineScope()
    val available by remember(episodeStreamState) {
        episodeStreamState?.available ?: flowOf(null)
    }.collectAsStateWithLifecycle(null)

    ElevatedCard(
        modifier = modifier,
        onClick = {
            scope.launch {
                val stream = episodeStreamState?.getStream() ?: return@launch

                withMainContext {
                    onStream(stream)
                }
            }
        },
        colors = CardDefaults.elevatedCardColors(
            containerColor = Platform.colorScheme().background,
            contentColor = Platform.colorScheme().onBackground
        ),
        enabled = available ?: true,
        elevation = CardDefaults.elevatedCardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val posters = remember(episode.showId, episode.id) { episode.posters(fallbackShow = null) }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .height(84.dp)
                    .aspectRatio(1.75F, true)
                    .clip(Platform.shapes().medium),
                contentAlignment = Alignment.Center
            ) {
                var fallback by remember { mutableStateOf(false) }

                if (fallback) {
                    MaterialSymbols(
                        name = MaterialSymbols.HIDE_IMAGE,
                        contentDescription = null,
                    )
                } else {
                    AsyncImage(
                        model = posters.firstOrNull(),
                        contentScale = ContentScale.Crop,
                        error = rememberNestedImagePainter(
                            models = posters.drop(1),
                            contentScale = ContentScale.Crop,
                            onError = {
                                fallback = true
                            }
                        ),
                        contentDescription = null
                    )
                }

                episode.runtime.takeIf { it > 0 }?.let { runtime ->
                    Text(
                        modifier = Modifier
                            .padding(end = 4.dp, bottom = 4.dp)
                            .align(Alignment.BottomEnd)
                            .background(
                                color = Platform.colorScheme().secondaryContainer,
                                shape = Platform.shapes().small
                            )
                            .padding(4.dp),
                        text = runtime.toDuration(DurationUnit.MINUTES).toString(),
                        maxLines = 1,
                        color = Platform.colorScheme().onSecondaryContainer,
                        style = Platform.typography().labelSmall
                    )
                }
            }

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