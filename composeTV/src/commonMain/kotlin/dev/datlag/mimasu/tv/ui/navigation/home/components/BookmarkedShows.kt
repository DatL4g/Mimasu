package dev.datlag.mimasu.tv.ui.navigation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderDefaults
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.fade
import com.eygraber.compose.placeholder.placeholder
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.common.color
import dev.datlag.mimasu.tv.common.fadeHighlightColor
import dev.datlag.mimasu.tv.tv_home_your_series
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.viewmodel.FirebaseViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.ifTrue
import io.tolgee.stringResource

@Composable
internal fun LazyItemScope.BookmarkedShows(
    paddingValues: PaddingValues,
    listState: LazyListState,
    onClick: (Show) -> Unit
) {
    val firebaseViewModel = kodeinViewModel<FirebaseViewModel>()
    val bookmarked = firebaseViewModel.bookmarkedShows.collectAsLazyPagingItems()
    val hasBookmarks by firebaseViewModel.hasBookmarkedShows.collectAsState(false)
    var focusedShow by remember { mutableStateOf<Show?>(null) }

    if (hasBookmarks || bookmarked.itemCount > 0) {
        Box(
            modifier = Modifier
                .fillParentMaxWidth()
                .ifTrue(focusedShow != null) {
                    fillParentMaxHeight(0.9F).animateContentSize()
                }
                .ifFalse(focusedShow != null) {
                    padding(paddingValues)
                }
        ) {
            val backdrops = remember(focusedShow) { focusedShow.backdrops(fallback = null) }
            val posters = remember(focusedShow) { focusedShow.posters(fallbackShow = null) }
            val gradientColor = MaterialTheme.colorScheme.background

            AsyncImage(
                model = backdrops.firstOrNull(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                error = rememberNestedImagePainter(
                    models = backdrops.drop(1),
                    contentScale = ContentScale.Crop,
                    error = rememberNestedImagePainter(
                        models = posters,
                        contentScale = ContentScale.Crop
                    )
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth(0.55F)
                        .padding(paddingValues)
                        .padding(start = 32.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                ) {
                    val title = remember(focusedShow?.name) {
                        focusedShow?.name?.ifBlank { null }
                    }
                    val originalTitle = remember(focusedShow?.originalName, title) {
                        focusedShow?.originalName?.ifBlank { null }?.takeUnless {
                            it.equals(title, ignoreCase = true)
                        }
                    }
                    val tagline = remember(focusedShow?.tagline) {
                        focusedShow?.tagline?.ifBlank { null } ?: focusedShow?.originalTagline?.ifBlank { null }
                    }

                    Text(
                        modifier = Modifier.fillMaxWidth(),
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
                }
                if (focusedShow == null) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 32.dp,
                                start = 32.dp,
                                end = 32.dp
                            ),
                        text = stringResource(Res.string.tv_home_your_series),
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1
                    )
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 16.dp
                    )
                ) {
                    items(bookmarked.itemCount) { index ->
                        val show = bookmarked[index]

                        BookmarkCard(
                            show = show,
                            onClick = onClick,
                            onFocus = {
                                focusedShow = it ?: if (focusedShow == show) {
                                    null
                                } else {
                                    focusedShow
                                }

                                if (it != null) {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        )
                    }
                    when {
                        bookmarked.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                BookmarkCard(show = null)
                            }
                        }
                        bookmarked.loadState.append is LoadState.Loading -> {
                            items(3) {
                                BookmarkCard(show = null)
                            }
                        }
                    }
                }
            }
        }
    } else {
        Spacer(
            modifier = Modifier
                .height(height = paddingValues.calculateTopPadding() + 32.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(MainThread::class)
@Composable
private fun BookmarkCard(
    show: Show?,
    onClick: (Show) -> Unit = { },
    onFocus: suspend (Show?) -> Unit = { }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedMain(isFocused) {
        if (isFocused) {
            onFocus(show)
        } else {
            onFocus(null)
        }
    }

    Card(
        modifier = Modifier
            .width(200.dp)
            .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
        onClick = { show?.let(onClick) },
        interactionSource = interactionSource
    ) {
        val backdrops = remember(show) { show.backdrops(fallback = null) }
        val posters = remember(show) { show.posters(fallbackShow = null) }

        AsyncImage(
            modifier = Modifier.fillMaxSize().placeholder(
                visible = show == null,
                highlight = PlaceholderHighlight.fade(
                    highlightColor = PlaceholderDefaults.fadeHighlightColor()
                ),
                color = PlaceholderDefaults.color()
            ),
            model = backdrops.firstOrNull(),
            error = rememberNestedImagePainter(
                models = backdrops.drop(1),
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = posters,
                    contentScale = ContentScale.Crop
                )
            ),
            contentScale = ContentScale.Crop,
            contentDescription = show?.name
        )
    }
}