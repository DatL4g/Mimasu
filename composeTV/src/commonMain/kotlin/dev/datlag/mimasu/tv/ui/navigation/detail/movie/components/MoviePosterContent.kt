package dev.datlag.mimasu.tv.ui.navigation.detail.movie.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.IconButton
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderDefaults
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.fade
import com.eygraber.compose.placeholder.placeholder
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.core.YouTubeUtils
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.common.color
import dev.datlag.mimasu.tv.common.fadeHighlightColor
import dev.datlag.mimasu.tv.tv_movie_rating
import dev.datlag.mimasu.tv.tv_movie_rating_placeholder
import dev.datlag.mimasu.tv.tv_movie_release_date
import dev.datlag.mimasu.tv.tv_movie_release_date_format
import dev.datlag.mimasu.tv.tv_movie_runtime
import dev.datlag.mimasu.tv.tv_movie_watch_trailer
import dev.datlag.mimasu.ui.common.formatMedium
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.rememberAdjustableState
import dev.datlag.mimasu.ui.viewmodel.FirebaseViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.async.VirtualIO
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import io.tolgee.stringResource
import kotlinx.coroutines.Dispatchers
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@OptIn(MainThread::class)
@Composable
internal fun MoviePosterContent(
    movie: Movie?,
    initial: CommonMovie?,
    listState: LazyListState,
    loggedIn: Boolean,
    modifier: Modifier = Modifier,
    onLogin: () -> Unit
) {
    val firebaseViewModel = kodeinViewModel<FirebaseViewModel>()

    Box(
        modifier = modifier
    ) {
        val backdrops = remember(movie, initial) { movie.backdrops(fallbackMovie = initial) }
        val gradientColor = MaterialTheme.colorScheme.background
        val trailer = remember(movie) {
            movie?.youtubeTrailer(
                language = Locale.current.language,
                country = Locale.current.region
            )
        }
        val trailerUrl = remember(trailer) {
            trailer?.let { YouTubeUtils.videoUrl(it.key) }
        }
        val uriHandler = LocalUriHandler.current

        AsyncImage(
            model = backdrops.firstOrNull(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            error = rememberNestedImagePainter(
                models = backdrops.drop(1),
                contentScale = ContentScale.Crop,
            ),
            modifier = Modifier.matchParentSize().drawWithContent {
                drawContent()
                drawRect(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, gradientColor),
                        startY = 300f
                    )
                )
                drawRect(
                    Brush.horizontalGradient(
                        colors = listOf(gradientColor, Color.Transparent),
                        endX = 1000f,
                        startX = 300f
                    )
                )
                drawRect(
                    Brush.linearGradient(
                        colors = listOf(gradientColor, Color.Transparent),
                        start = Offset(x = 500f, y = 500f),
                        end = Offset(x = 1000f, y = 0f)
                    )
                )
            }
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.55F).fillMaxHeight().padding(start = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
        ) {
            val title = remember(movie?.title, initial?.title) {
                movie?.title?.ifBlank { null } ?: initial?.title?.ifBlank { null }
            }
            val originalTitle = remember(movie?.originalTitle, initial?.originalTitle, title) {
                (movie?.originalTitle?.ifBlank { null } ?: initial?.originalTitle?.ifBlank { null }).takeUnless {
                    it.equals(title, ignoreCase = true)
                }
            }
            val tagline = remember(movie?.tagline, movie?.originalTagline) {
                movie?.tagline?.ifBlank { null } ?: movie?.originalTagline?.ifBlank { null }
            }

            Text(
                modifier = Modifier.fillMaxWidth().placeholder(
                    visible = title.isNullOrBlank(),
                    shape = MaterialTheme.shapes.small,
                    highlight = PlaceholderHighlight.fade(
                        highlightColor = PlaceholderDefaults.fadeHighlightColor()
                    ),
                    color = PlaceholderDefaults.color()
                ),
                text = title ?: "",
                style = MaterialTheme.typography.displayMedium,
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
            originalTitle?.let {
                Text(
                    text = it,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }
            tagline?.let {
                Text(
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    text = it,
                    maxLines = 3,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.padding(top = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                val releaseLocalDate = remember(movie?.releaseLocalDate, initial?.releaseLocalDate) {
                    movie?.releaseLocalDate ?: initial?.releaseLocalDate
                }
                val releaseDate = remember(movie?.releaseDate, initial?.releaseDate) {
                    movie?.releaseDate?.ifBlank { null } ?: initial?.releaseDate?.ifBlank { null }
                }
                val voteAverage = remember(movie?.voteAverage, initial?.voteAverage) {
                    movie?.voteAverage?.takeIf { it > 0F } ?: initial?.voteAverage?.takeIf { it > 0F }
                }

                releaseLocalDate.formatMedium(
                    fallbackFormat = Res.string.tv_movie_release_date_format,
                    fallbackValue = releaseDate
                )?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.tv_movie_release_date),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                movie?.runtime?.takeIf { it > 0 }?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.tv_movie_runtime),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = it.toDuration(DurationUnit.MINUTES).toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                voteAverage?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.tv_movie_rating),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = stringResource(Res.string.tv_movie_rating_placeholder, (it * 10F).roundToInt()),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var bookmarked by rememberAdjustableState(
                    initialValue = false,
                    key1 = movie?.id,
                    key2 = initial?.id,
                    key3 = loggedIn,
                    context = Dispatchers.VirtualIO
                ) { current ->
                    val id = movie?.id?.takeIf { it > 0 } ?: initial?.id?.takeIf { it > 0 }

                    id?.let {
                        firebaseViewModel.isMovieBookmarked(it)
                    } ?: current
                }
                val trailerInteraction = remember { MutableInteractionSource() }
                val isTrailerFocused by trailerInteraction.collectIsFocusedAsState()

                LaunchedMain(isTrailerFocused) {
                    if (isTrailerFocused) {
                        listState.animateScrollToItem(0)
                    }
                }

                Button(
                    onClick = {
                        trailerUrl?.let(uriHandler::openUri)
                    },
                    enabled = !trailerUrl.isNullOrBlank(),
                    interactionSource = trailerInteraction
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.PLAY_ARROW,
                        contentDescription = null,
                        filled = true,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.tv_movie_watch_trailer))
                }
                IconButton(
                    onClick = {
                        if (loggedIn) {
                            bookmarked = !bookmarked

                            if (movie != null) {
                                firebaseViewModel.bookmark(bookmarked, movie)
                            }
                        } else {
                            onLogin()
                        }
                    },
                    enabled = movie != null
                ) {
                    if (bookmarked) {
                        MaterialSymbols(
                            name = MaterialSymbols.BOOKMARK,
                            contentDescription = null,
                            filled = true
                        )
                    } else {
                        MaterialSymbols(
                            name = MaterialSymbols.BOOKMARK_ADD,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
