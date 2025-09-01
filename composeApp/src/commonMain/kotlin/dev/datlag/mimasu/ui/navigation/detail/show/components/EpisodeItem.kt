package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.firebase.firestore.ShowData
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.RevealingCard
import dev.datlag.mimasu.ui.other.EpisodeStreamState
import dev.datlag.mimasu.ui.other.ShowState
import dev.datlag.mimasu.ui.other.rememberEpisodeStream
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.VirtualIO
import dev.datlag.tooling.async.launchVirtualIO
import dev.datlag.tooling.async.withMainContext
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.datlag.mimasu.extension.model.Show as Extension

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EpisodeItem(
    tmdbId: Int?,
    episode: Season.Episode,
    episodeData: ShowData.EpisodeData?,
    seasonNumber: Int?,
    showAvailability: ShowState,
    loggedIn: Boolean,
    clickBlocked: Boolean,
    modifier: Modifier = Modifier,
    onDialog: () -> Unit,
    onStream: (Extension.Response) -> Unit,
    markAsWatched: suspend () -> Unit,
    markAsUnWatched: suspend () -> Unit,
    onLogin: () -> Unit,
    blockClick: (Boolean) -> Unit
) {
    val episodeStream = rememberEpisodeStream(
        showState = showAvailability,
        tmdbId = tmdbId,
        seasonNumber = seasonNumber,
        episode = episode
    )
    val scope = rememberCoroutineScope()
    val episodeStreamState by episodeStream.state.collectAsStateWithLifecycle()

    var isRevealed by remember(tmdbId, seasonNumber, episode.id) { mutableStateOf(false) }
    val watched = remember(episodeData) {
        if (episodeData?.markedAsWatched == false) {
            false
        } else {
            episodeData?.markedAsWatched == true || episodeData?.finished == true
        }
    }

    RevealingCard(
        modifier = modifier,
        isRevealed = isRevealed,
        onCardClick = {
            scope.launchVirtualIO {
                blockClick(true)
                val stream = episodeStream.getStream() ?: return@launchVirtualIO run {
                    blockClick(false)
                    onDialog()
                }
                blockClick(false)

                withMainContext {
                    onStream(stream)
                }
            }
        },
        cardColors = CardDefaults.cardColors(
            containerColor = Platform.colorScheme().background,
            contentColor = Platform.colorScheme().onBackground
        ),
        revealedCardColors = CardDefaults.cardColors(),
        cardEnabled = !clickBlocked && !isRevealed && when (val current = episodeStreamState) {
            is EpisodeStreamState.Available -> current.state
            else -> current !is EpisodeStreamState.Unavailable
        },
        actionsContent = {
            IconButton(
                modifier = Modifier.padding(start = 4.dp).fillMaxHeight(),
                onClick = {
                    isRevealed = false
                    if (loggedIn) {
                        scope.launchVirtualIO {
                            if (watched) {
                                markAsUnWatched()
                            } else {
                                markAsWatched()
                            }
                        }
                    } else {
                        onLogin()
                    }
                },
                shapes = IconButtonDefaults.shapes(),
                colors = if (watched) {
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = Platform.colorScheme().error
                    )
                } else {
                    IconButtonDefaults.filledIconButtonColors()
                }
            ) {
                MaterialSymbols(
                    name = if (watched) {
                        MaterialSymbols.CLOSE
                    } else {
                        MaterialSymbols.CHECK
                    },
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = {
                    isRevealed = false
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledTonalIconButtonColors()
            ) {
                MaterialSymbols(
                    name = MaterialSymbols.LOGOUT,
                    contentDescription = null
                )
            }
        },
        cardContent = {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val posters = remember(episode.showId, episode.id) { episode.posters(fallbackShow = null) }

                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
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
                            .padding(4.dp)
                            .align(Alignment.BottomEnd),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (watched) {
                            MaterialSymbols(
                                modifier = Modifier.size(24.dp).background(
                                    color = Platform.colorScheme().secondaryContainer,
                                    shape = Platform.shapes().small
                                ).padding(4.dp),
                                name = MaterialSymbols.CHECK,
                                contentDescription = null,
                                filled = true,
                                tint = Platform.colorScheme().onSecondaryContainer,
                            )
                        }
                        Spacer(modifier = Modifier.weight(1F))
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
                    modifier = Modifier.weight(1F).padding(4.dp),
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

                IconButton(
                    onClick = {
                        isRevealed = !isRevealed
                    }
                ) {
                    MaterialSymbols(
                        name = "more_vert",
                        contentDescription = null
                    )
                }
            }
        }
    )
}