package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.common.hazeEffect
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.CollapsingToolbar
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.rememberAdjustableState
import dev.datlag.mimasu.ui.viewmodel.FirebaseViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.VirtualIO
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieToolbar(
    appBarState: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    hazeState: HazeState,
    listState: LazyListState,
    movie: Movie?,
    initial: CommonMovie?,
    loggedIn: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onLogin: () -> Unit
) {
    val firebaseViewModel = kodeinViewModel<FirebaseViewModel>()

    CollapsingToolbar(
        state = appBarState,
        scrollBehavior = scrollBehavior,
        modifier = modifier.hazeEffect(
            state = hazeState,
            listState = listState
        ),
        background = { state ->
            val backdrops = remember(movie, initial) { movie.backdrops(initial) }

            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .matchParentSize(),
                model = backdrops.firstOrNull(),
                error = rememberNestedImagePainter(
                    models = backdrops.drop(1),
                    contentScale = ContentScale.Crop
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = state.expandProgress
            )
        },
        navigationIcon = { state ->
            IconButton(
                modifier = if (state.isCollapsed) {
                    Modifier
                } else {
                    Modifier.background(
                        Platform.colorScheme().surface.copy(alpha = state.expandProgress * 0.5F),
                        CircleShape
                    )
                },
                onClick = onBack
            ) {
                MaterialSymbols(
                    name = MaterialSymbols.ARROW_BACK_IOS_NEW,
                    contentDescription = null,
                )
            }
        },
        title = { state ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                val mainTitle = remember(movie?.id, initial?.id) {
                    movie?.title?.ifBlank { null } ?: initial?.title?.ifBlank { null }
                }
                val subTitle = remember(movie?.id, initial?.id, mainTitle) {
                    (movie?.originalTitle?.ifBlank { null } ?: initial?.originalTitle?.ifBlank { null }).takeUnless {
                        it.equals(mainTitle, ignoreCase = true)
                    }
                }
                val finalMainTitle = remember(mainTitle, subTitle) {
                    mainTitle?.ifBlank { null } ?: subTitle?.ifBlank { null } ?: ""
                }
                val finalSubTitle = remember(subTitle, finalMainTitle) {
                    subTitle.takeUnless { it.equals(finalMainTitle, ignoreCase = true) }
                }

                Text(
                    modifier = Modifier
                        .placeholder(
                            visible = finalMainTitle.isBlank(),
                            highlight = PlaceholderHighlight.fade()
                        ),
                    text = finalMainTitle,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = if (!state.isCollapsed) {
                        LocalTextStyle.current.copy(
                            shadow = Shadow(
                                color = Platform.colorScheme().surface,
                                offset = Offset(4F, 4F),
                                blurRadius = 8F
                            )
                        )
                    } else {
                        LocalTextStyle.current
                    }
                )
                if (!finalSubTitle.isNullOrBlank()) {
                    Text(
                        text = finalSubTitle,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = if (!state.isCollapsed) {
                            Platform.typography().labelMedium.copy(
                                shadow = Shadow(
                                    color = Platform.colorScheme().surface,
                                    offset = Offset(4F, 4F),
                                    blurRadius = 8F
                                )
                            )
                        } else {
                            Platform.typography().labelMedium
                        }
                    )
                }
            }
        },
        actions = { state ->
            Row(
                modifier = Modifier
                    .animateContentSize()
                    .ifFalse(state.isCollapsed) {
                        background(Platform.colorScheme().surface.copy(alpha = 0.5F), CircleShape)
                    },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
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
                        firebaseViewModel.isMovieBookmarked(id)
                    } ?: current
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
                    }
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
    )
}