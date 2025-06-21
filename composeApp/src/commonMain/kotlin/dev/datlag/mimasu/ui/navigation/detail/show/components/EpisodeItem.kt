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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import dev.datlag.mimasu.ui.custom.swipe.SwipeAction
import dev.datlag.mimasu.ui.custom.swipe.SwipeableActionsBox
import dev.datlag.mimasu.ui.custom.swipe.rememberSwipeableActionsState
import dev.datlag.mimasu.ui.navigation.detail.show.EpisodeStreamState
import dev.datlag.mimasu.ui.navigation.detail.show.ShowState
import dev.datlag.mimasu.ui.navigation.detail.show.rememberEpisodeStream
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
fun EpisodeItem(
    tmdbId: Int?,
    episode: Season.Episode,
    seasonNumber: Int?,
    showAvailability: ShowState,
    modifier: Modifier = Modifier,
    onStream: (Extension.Response) -> Unit
) {
    val episodeStream = rememberEpisodeStream(
        showState = showAvailability,
        tmdbId = tmdbId,
        seasonNumber = seasonNumber,
        episode = episode
    )
    val scope = rememberCoroutineScope()
    val episodeStreamState by episodeStream.state.collectAsStateWithLifecycle()

    var finished by remember { mutableStateOf(false) }
    val errorColor = Platform.colorScheme().error
    val onError = Platform.colorScheme().onError
    val successColor = Color(0xFF66BB6A)
    val onSuccess = Color.White
    val startActions = remember(finished) {
        if (finished) {
            listOf(
                SwipeAction(
                    onSwipe = { finished = false },
                    icon = {
                        MaterialSymbols(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            name = MaterialSymbols.CLOSE,
                            contentDescription = null,
                            tint = onError
                        )
                    },
                    background = errorColor,
                    isUndo = true
                )
            )
        } else {
            emptyList()
        }
    }
    val endActions = remember(finished) {
        if (finished) {
            emptyList()
        } else {
            listOf(
                SwipeAction(
                    onSwipe = { finished = true },
                    icon = {
                        MaterialSymbols(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            name = MaterialSymbols.CHECK,
                            contentDescription = null,
                            filled = true,
                            tint = onSuccess
                        )
                    },
                    background = successColor,
                    isUndo = false
                )
            )
        }
    }

    SwipeableActionsBox(
        modifier = modifier.clip(CardDefaults.elevatedShape),
        state = rememberSwipeableActionsState(),
        startActions = startActions,
        endActions = endActions,
        backgroundUntilSwipeThreshold = Platform.colorScheme().surfaceColorAtElevation(32.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    val stream = episodeStream.getStream() ?: return@launch

                    withMainContext {
                        onStream(stream)
                    }
                }
            },
            shape = RectangleShape,
            colors = CardDefaults.elevatedCardColors(
                containerColor = Platform.colorScheme().background,
                contentColor = Platform.colorScheme().onBackground
            ),
            enabled = when  (val current = episodeStreamState) {
                is EpisodeStreamState.Available -> current.state
                else -> current !is EpisodeStreamState.Unavailable
            },
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

                    Row(
                        modifier = Modifier
                            .padding(end = 4.dp, bottom = 4.dp)
                            .align(Alignment.BottomEnd),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (val current = episodeStreamState) {
                            is EpisodeStreamState.Requesting -> CircularProgressIndicator(
                                modifier = Modifier.size(24.dp).background(
                                    color = Platform.colorScheme().secondaryContainer,
                                    shape = Platform.shapes().small
                                ).padding(4.dp),
                                color = Platform.colorScheme().onSecondaryContainer,
                                strokeWidth = 2.dp
                            )
                            is EpisodeStreamState.Available -> MaterialSymbols(
                                modifier = Modifier.size(24.dp).background(
                                    color = Platform.colorScheme().secondaryContainer,
                                    shape = Platform.shapes().small
                                ).padding(4.dp),
                                name = if (current.state) {
                                    MaterialSymbols.PLAY_ARROW
                                } else {
                                    MaterialSymbols.WARNING
                                },
                                contentDescription = null,
                                filled = true,
                                tint = Platform.colorScheme().onSecondaryContainer,
                            )
                            else -> { }
                        }
                        episode.runtime.takeIf { it > 0 }?.let { runtime ->
                            Text(
                                modifier = Modifier.height(24.dp).background(
                                    color = Platform.colorScheme().secondaryContainer,
                                    shape = Platform.shapes().small
                                ).padding(4.dp),
                                text = runtime.toDuration(DurationUnit.MINUTES).toString(),
                                maxLines = 1,
                                color = Platform.colorScheme().onSecondaryContainer,
                                style = Platform.typography().labelSmall
                            )
                        }
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
}