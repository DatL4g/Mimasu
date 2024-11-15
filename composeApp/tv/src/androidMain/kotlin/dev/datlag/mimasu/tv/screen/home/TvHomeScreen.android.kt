package dev.datlag.mimasu.tv.screen.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Carousel
import androidx.tv.material3.CompactCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tv.LocalTVPadding
import dev.datlag.mimasu.tv.common.ifElse
import dev.datlag.mimasu.tv.common.immersiveListGradient
import dev.datlag.mimasu.tv.common.requestFocusOnFirstGainingVisibility
import dev.datlag.mimasu.tv.ui.ImmersiveList
import dev.datlag.mimasu.tv.ui.MediaCard
import dev.datlag.mimasu.tv.ui.MediaRowItem
import dev.datlag.tooling.scopeCatching
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class
)
@Composable
actual fun TvHomeScreen(component: TvHomeComponent) {
    LazyColumn(
        modifier = Modifier.padding(LocalTVPadding.current),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stickyHeader {
            Text(
                modifier = Modifier.fillParentMaxWidth(),
                text = "Trending Movies Today"
            )
        }
        item { 
            MovieCatalog(flow = component.trendingDayMovies)
        }
        stickyHeader {
            Text(
                modifier = Modifier.fillParentMaxWidth(),
                text = "Installed Provider"
            )
        }
        item {
            Button(
                onClick = {
                    component.watchVideo()
                }
            ) {
                Text("Watch Video")
            }
        }
        item {
            Text(text = "Netflix: ${component.netflixInstalled}")
        }
        item {
            Text(text = "Disney Plus: ${component.disneyPlusInstalled}")
        }
        item {
            Text(text = "Amazon Prime: ${component.amazonPrimeVideoInstalled}")
        }
        item {
            Text(text = "Burning-Series: ${component.burningSeriesInstalled}")
        }
        item {
            Text(text = "Crunchyroll: ${component.crunchyRollInstalled}")
        }
        item {
            Text(text = "Paramount Plus: ${component.paramountPlusInstalled}")
        }
    }
}

@Composable
private fun MovieCatalog(flow: Flow<PagingData<Trending.Response.Media.Movie>>) {
    val trendingMovies = flow.collectAsLazyPagingItems()

    var isListFocused by remember {
        mutableStateOf(false)
    }
    var selectedMovie by remember(trendingMovies) {
        mutableStateOf(scopeCatching { trendingMovies.peek(1) }.getOrNull())
    }

    val sectionTitle = if (isListFocused) {
        null
    } else {
        "Trending Movies"
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(trendingMovies.itemCount) { index ->
            MediaRowItem(
                index = index,
                movie = trendingMovies[index]!!,
                onMovieSelected = { },
                showItemTitle = true
            )
        }
        trendingMovies.apply { 
            when {
                loadState.refresh is LoadStateLoading -> {
                    item {
                        Text(text = "Loading Refresh")
                    }
                }
                loadState.refresh is LoadStateError -> {
                    item {
                        Text(text = "Error Refresh")
                    }
                }
                loadState.refresh is LoadStateNotLoading -> {
                    item {
                        Text(text = "Not Loading Refresh")
                    }
                }
                loadState.append is LoadStateLoading -> {
                    item {
                        Text(text = "Loading Append")
                    }
                }
                loadState.append is LoadStateError -> {
                    item {
                        Text(text = "Error Append")
                    }
                }
                loadState.append is LoadStateNotLoading -> {
                    item {
                        Text(text = "Not Loading Append")
                    }
                }
            }
        }
    }
}

@Composable
private fun TVCatalog(flow: Flow<PagingData<Trending.Response.Media.TV>>) {
    val trendingShows = flow.collectAsLazyPagingItems()


    var isListFocused by remember {
        mutableStateOf(false)
    }
    var selectedShow by remember(trendingShows) {
        mutableStateOf(scopeCatching { trendingShows.peek(1) }.getOrNull())
    }

    val sectionTitle = if (isListFocused) {
        null
    } else {
        "Trending Shows"
    }

    ImmersiveList(
        selectedShow = selectedShow,
        isListFocused = isListFocused,
        gradientColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7F),
        shows = trendingShows.itemSnapshotList.filterNotNull().toImmutableSet(),
        sectionTitle = sectionTitle,
        onFocusChanged = {
            isListFocused = it.hasFocus
        },
        onShowFocused = {
            selectedShow = it
        },
        onShowClick = {}
    )
}