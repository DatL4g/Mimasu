package dev.datlag.mimasu.tv.ui.navigation.detail.show.components

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderDefaults
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.fade
import com.eygraber.compose.placeholder.placeholder
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.common.color
import dev.datlag.mimasu.tv.common.fadeHighlightColor
import dev.datlag.mimasu.tv.tv_show_average_runtime
import dev.datlag.mimasu.tv.tv_show_bookmark
import dev.datlag.mimasu.tv.tv_show_bookmarked
import dev.datlag.mimasu.tv.tv_show_first_air_date
import dev.datlag.mimasu.tv.tv_show_first_air_date_format
import dev.datlag.mimasu.tv.tv_show_rating
import dev.datlag.mimasu.tv.tv_show_rating_placeholder
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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(MainThread::class)
@Composable
internal fun ShowPosterContent(
    show: Show?,
    initial: TV?,
    listState: LazyListState,
    loggedIn: Boolean,
    modifier: Modifier = Modifier,
    onLogin: () -> Unit
) {
    val firebaseViewModel = kodeinViewModel<FirebaseViewModel>()

    Box(
        modifier = modifier
    ) {
        val backdrops = remember(show, initial) { show.backdrops(fallback = initial) }
        val gradientColor = MaterialTheme.colorScheme.background

        AsyncImage(
            model = backdrops.firstOrNull(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            error = rememberNestedImagePainter(
                models = backdrops.drop(1),
                contentScale = ContentScale.Crop
            ),
            modifier = Modifier.matchParentSize().drawWithContent {
                drawContent()
                drawRect(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, gradientColor),
                        startY = 300F
                    )
                )
                drawRect(
                    Brush.horizontalGradient(
                        colors = listOf(gradientColor, Color.Transparent),
                        endX = 1000F,
                        startX = 300F
                    )
                )
                drawRect(
                    Brush.linearGradient(
                        colors = listOf(gradientColor, Color.Transparent),
                        start = Offset(x = 500F, y = 500F),
                        end = Offset(x = 1000F, y = 0F)
                    )
                )
            }
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.55F).fillMaxHeight().padding(start = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
        ) {
            val title = remember(show?.name, initial?.name) {
                show?.name?.ifBlank { null } ?: initial?.name?.ifBlank { null }
            }
            val originalTitle = remember(show?.originalName, initial?.originalName) {
                (show?.originalName?.ifBlank { null } ?: initial?.originalName?.ifBlank { null }).takeUnless {
                    it.equals(title, ignoreCase = true)
                }
            }
            val tagline = remember(show?.tagline, show?.originalTagline) {
                show?.tagline?.ifBlank { null } ?: show?.originalTagline?.ifBlank { null }
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
                val firstAirLocalDate = remember(show?.firstAirLocalDate, initial?.firstAirLocalDate) {
                    show?.firstAirLocalDate ?: initial?.firstAirLocalDate
                }
                val firstAirDate = remember(show?.firstAirDate, initial?.firstAirDate) {
                    show?.firstAirDate?.ifBlank { null } ?: initial?.firstAirDate?.ifBlank { null }
                }
                val voteAverage = remember(show?.voteAverage, initial?.voteAverage) {
                    show?.voteAverage?.takeIf { it > 0F } ?: initial?.voteAverage?.takeIf { it > 0F }
                }

                firstAirLocalDate.formatMedium(
                    fallbackFormat = Res.string.tv_show_first_air_date_format,
                    fallbackValue = firstAirDate
                )?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.tv_show_first_air_date),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
                show?.runtimeAverage?.takeIf { it > 0 }?.let {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.tv_show_average_runtime),
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
                            text = stringResource(Res.string.tv_show_rating),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = stringResource(Res.string.tv_show_rating_placeholder, (it * 10F).roundToInt()),
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
                    key1 = show?.id,
                    key2 = initial?.id,
                    key3 = loggedIn,
                    context = Dispatchers.VirtualIO
                ) { current ->
                    val id = show?.id?.takeIf { it > 0 } ?: initial?.id?.takeIf { it > 0 }

                    id?.let {
                        firebaseViewModel.isShowBookmarked(it)
                    } ?: current
                }
                val buttonInteraction = remember { MutableInteractionSource() }
                val isFocused by buttonInteraction.collectIsFocusedAsState()
                val scope = rememberCoroutineScope()

                LaunchedMain(isFocused) {
                    if (isFocused) {
                        listState.animateScrollToItem(0)
                    }
                }

                Button(
                    onClick = {
                        if (loggedIn) {
                            bookmarked = !bookmarked

                            if (show != null) {
                                scope.launch {
                                    firebaseViewModel.bookmark(bookmarked, show)
                                }
                            }
                        } else {
                            onLogin()
                        }
                    },
                    interactionSource = buttonInteraction,
                    enabled = show != null
                ) {
                    if (bookmarked) {
                        MaterialSymbols(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = MaterialSymbols.BOOKMARK,
                            contentDescription = null,
                            filled = true
                        )
                    } else {
                        MaterialSymbols(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = MaterialSymbols.BOOKMARK_ADD,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = if (bookmarked) {
                        stringResource(Res.string.tv_show_bookmarked)
                    } else {
                        stringResource(Res.string.tv_show_bookmark)
                    })
                }
            }
        }
    }
}