package dev.datlag.mimasu.tv.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.api.Trending
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList

enum class ItemDirection(val aspectRatio: Float) {
    Vertical(10.5f / 16f),
    Horizontal(16f / 9f);
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MediaRow(
    movies: ImmutableCollection<Trending.Response.Media.Movie>,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    onMovieSelected: (Trending.Response.Media.Movie) -> Unit = { }
) {
    val (lazyRow, firstItem) = remember {
        FocusRequester.createRefs()
    }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1F)
                    .padding(start = startPadding, top = 16.dp, bottom = 16.dp)
            )
        }
        AnimatedContent(targetState = movies) { movieState ->
            LazyRow(
                contentPadding = PaddingValues(
                    start = startPadding,
                    end = endPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer { firstItem }
            ) {
                itemsIndexed(movieState.toImmutableList(), key = { _, movie -> movie.id }) { index, movie ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }

                    MediaRowItem(
                        modifier = itemModifier.weight(1F),
                        index = index,
                        itemDirection = itemDirection,
                        onMovieSelected = {
                            lazyRow.saveFocusedChild()
                            onMovieSelected(it)
                        },
                        movie = movie,
                        showItemTitle = showItemTitle
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImmersiveListMediaRow(
    movies: ImmutableCollection<Trending.Response.Media.Movie>,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    onMovieSelected: (Trending.Response.Media.Movie) -> Unit = { },
    onMovieFocused: (Trending.Response.Media.Movie) -> Unit = { }
) {
    val (lazyRow, firstItem) = remember {
        FocusRequester.createRefs()
    }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = startPadding)
                    .padding(vertical = 16.dp)
            )
        }
        AnimatedContent(targetState = movies) { movieState ->
            LazyRow(
                contentPadding = PaddingValues(start = startPadding, end = endPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer {
                        firstItem
                    }
            ) {
                itemsIndexed(movieState.toImmutableList(), key = { _, movie -> movie.id }) { index, movie ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }

                    MediaRowItem(
                        modifier = itemModifier.weight(1f),
                        index = index,
                        itemDirection = itemDirection,
                        onMovieSelected = {
                            lazyRow.saveFocusedChild()
                            onMovieSelected(it)
                        },
                        onMovieFocused = onMovieFocused,
                        movie = movie,
                        showItemTitle = showItemTitle
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImmersiveListShowRow(
    shows: ImmutableCollection<Trending.Response.Media.TV>,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
    title: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    ),
    showItemTitle: Boolean = true,
    onShowSelected: (Trending.Response.Media.TV) -> Unit = { },
    onShowFocused: (Trending.Response.Media.TV) -> Unit = { }
) {
    val (lazyRow, firstItem) = remember {
        FocusRequester.createRefs()
    }

    Column(
        modifier = modifier.focusGroup()
    ) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = titleStyle,
                modifier = Modifier
                    .alpha(1f)
                    .padding(start = startPadding)
                    .padding(vertical = 16.dp)
            )
        }
        AnimatedContent(targetState = shows) { showState ->
            LazyRow(
                contentPadding = PaddingValues(start = startPadding, end = endPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .focusRequester(lazyRow)
                    .focusRestorer {
                        firstItem
                    }
            ) {
                itemsIndexed(showState.toImmutableList(), key = { _, show -> show.id }) { index, show ->
                    val itemModifier = if (index == 0) {
                        Modifier.focusRequester(firstItem)
                    } else {
                        Modifier
                    }

                    MediaRowItem(
                        modifier = itemModifier.weight(1f),
                        index = index,
                        itemDirection = itemDirection,
                        onMovieSelected = {
                            lazyRow.saveFocusedChild()
                            onShowSelected(it)
                        },
                        onMovieFocused = onShowFocused,
                        show = show,
                        showItemTitle = showItemTitle
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MediaRowItem(
    index: Int,
    movie: Trending.Response.Media.Movie,
    onMovieSelected: (Trending.Response.Media.Movie) -> Unit,
    showItemTitle: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    onMovieFocused: (Trending.Response.Media.Movie) -> Unit = { }
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    MediaCard(
        onClick = { onMovieSelected(movie) },
        title = {
            MediaRowItemText(
                showItemTitle = showItemTitle,
                isItemFocused = isFocused,
                movie = movie
            )
        },
        modifier = Modifier
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onMovieFocused(movie)
                }
            }
            .focusProperties {
                left = if (index == 0) {
                    FocusRequester.Cancel
                } else {
                    FocusRequester.Default
                }
            }
            .then(modifier)
            .width(if (itemDirection == ItemDirection.Horizontal) 196.dp else 96.dp)
            .aspectRatio(itemDirection.aspectRatio)
    ) {
        AsyncImage(
            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            model = if (itemDirection == ItemDirection.Horizontal) {
                movie.backdropPicture
            } else {
                movie.posterPicture
            },
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = if (itemDirection == ItemDirection.Horizontal) {
                    movie.backdropPictureW500
                } else {
                    movie.posterPictureW500
                },
                contentScale = ContentScale.Crop
            )
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun MediaRowItem(
    index: Int,
    show: Trending.Response.Media.TV,
    onMovieSelected: (Trending.Response.Media.TV) -> Unit,
    showItemTitle: Boolean,
    modifier: Modifier = Modifier,
    itemDirection: ItemDirection = ItemDirection.Horizontal,
    onMovieFocused: (Trending.Response.Media.TV) -> Unit = { }
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    MediaCard(
        onClick = { onMovieSelected(show) },
        title = {
            MediaRowItemText(
                showItemTitle = showItemTitle,
                isItemFocused = isFocused,
                show = show
            )
        },
        modifier = Modifier
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onMovieFocused(show)
                }
            }
            .focusProperties {
                left = if (index == 0) {
                    FocusRequester.Cancel
                } else {
                    FocusRequester.Default
                }
            }
            .then(modifier)
            .width(if (itemDirection == ItemDirection.Horizontal) 196.dp else 96.dp)
            .aspectRatio(itemDirection.aspectRatio)
    ) {
        AsyncImage(
            modifier = Modifier.aspectRatio(itemDirection.aspectRatio),
            model = if (itemDirection == ItemDirection.Horizontal) {
                show.backdropPicture
            } else {
                show.posterPicture
            },
            contentDescription = show.name,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = if (itemDirection == ItemDirection.Horizontal) {
                    show.backdropPictureW500
                } else {
                    show.posterPictureW500
                },
                contentScale = ContentScale.Crop
            )
        )
    }
}

@Composable
private fun MediaRowItemText(
    showItemTitle: Boolean,
    isItemFocused: Boolean,
    movie: Trending.Response.Media.Movie,
    modifier: Modifier = Modifier
) {
    if (showItemTitle) {
        val movieNameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f
        )
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = modifier
                .alpha(movieNameAlpha)
                .fillMaxWidth()
                .padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MediaRowItemText(
    showItemTitle: Boolean,
    isItemFocused: Boolean,
    show: Trending.Response.Media.TV,
    modifier: Modifier = Modifier
) {
    if (showItemTitle) {
        val movieNameAlpha by animateFloatAsState(
            targetValue = if (isItemFocused) 1f else 0f
        )
        Text(
            text = show.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            modifier = modifier
                .alpha(movieNameAlpha)
                .fillMaxWidth()
                .padding(top = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}