package dev.datlag.mimasu.tv.screen.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tv.LocalTVPadding
import dev.datlag.mimasu.tv.common.ifElse
import dev.datlag.mimasu.tv.common.immersiveListGradient
import dev.datlag.mimasu.tv.common.requestFocusOnFirstGainingVisibility
import dev.datlag.mimasu.tv.ui.ImmersiveList
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
actual fun TvHomeScreen(component: TvHomeComponent) {
    LazyColumn(
        modifier = Modifier.padding(LocalTVPadding.current),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { 
            MovieCatalog(component = component)
        }
        item { 
            TVCatalog(component = component)
        }
    }
}

@Composable
private fun MovieCatalog(component: TvHomeComponent) {
    val trendingMovies by component.trendingMovies.map {
        it?.results?.mapNotNull { m -> m as? Trending.Response.Media.Movie }
    }.collectAsStateWithLifecycle(
        initialValue = null
    )


    var isListFocused by remember {
        mutableStateOf(false)
    }
    var selectedMovie by remember(trendingMovies) {
        mutableStateOf(trendingMovies?.firstOrNull())
    }

    val sectionTitle = if (isListFocused) {
        null
    } else {
        "Trending Movies"
    }

    ImmersiveList(
        selectedMovie = selectedMovie,
        isListFocused = isListFocused,
        gradientColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7F),
        movies = trendingMovies?.toImmutableSet() ?: persistentSetOf(),
        sectionTitle = sectionTitle,
        onFocusChanged = {
            isListFocused = it.hasFocus
        },
        onMovieFocused = {
            selectedMovie = it
        },
        onMovieClick = {}
    )
}

@Composable
private fun TVCatalog(component: TvHomeComponent) {
    val trendingShows by component.trendingSeries.map {
        it?.results?.mapNotNull { m -> m as? Trending.Response.Media.TV }
    }.collectAsStateWithLifecycle(
        initialValue = null
    )


    var isListFocused by remember {
        mutableStateOf(false)
    }
    var selectedShow by remember(trendingShows) {
        mutableStateOf(trendingShows?.firstOrNull())
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
        shows = trendingShows?.toImmutableSet() ?: persistentSetOf(),
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