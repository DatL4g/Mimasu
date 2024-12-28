package dev.datlag.mimasu.ui.navigation.screen.movie

import VideoPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.vanniktech.locale.Locale
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.mimasu.LocalHaze
import dev.datlag.mimasu.common.default
import dev.datlag.mimasu.common.hazeChild
import dev.datlag.mimasu.common.localized
import dev.datlag.mimasu.common.youtubeTrailer
import dev.datlag.mimasu.tmdb.model.DetailState
import dev.datlag.mimasu.ui.custom.component.IconText
import dev.datlag.mimasu.ui.navigation.screen.movie.component.DescriptionSection
import dev.datlag.mimasu.ui.navigation.screen.movie.component.DetailSuccess
import dev.datlag.mimasu.ui.navigation.screen.movie.component.MovieToolbar
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformBorder
import dev.datlag.tooling.compose.platform.PlatformClickableChipBorder
import dev.datlag.tooling.compose.platform.PlatformClickableChipColors
import dev.datlag.tooling.compose.platform.PlatformSuggestionChip
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun MovieScreen(component: MovieComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val movieState by component.movie.collectAsStateWithLifecycle(DetailState.Loading)
    val listState = rememberLazyListState()

    val movieTitle = remember(movieState) {
        when (val current = movieState) {
            is DetailState.Success -> current.data.title.ifBlank { null } ?: component.trending.title
            else -> component.trending.title
        }
    }

    val movieSubTitle = remember(movieState) {
        when (val current = movieState) {
            is DetailState.Success -> current.data.alternativeTitle?.ifBlank { null } ?: component.trending.alternativeTitle
            else -> component.trending.alternativeTitle
        }
    }

    val movieBackdrops = remember(movieState) {
        when (val current = movieState) {
            is DetailState.Success -> persistentListOf(
                current.data.backdropPicture,
                component.trending.backdropPicture,
                current.data.backdropPictureW500,
                component.trending.backdropPictureW500
            )
            else -> persistentListOf(
                component.trending.backdropPicture,
                component.trending.backdropPictureW500
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MovieToolbar(
                modifier = Modifier.hazeChild(
                    listState = listState
                ),
                appBarState = appBarState,
                scrollBehavior = scrollBehavior,
                title = movieTitle,
                subTitle = movieSubTitle,
                backdrops = movieBackdrops,
                onBack = component::back
            )
        }
    ) { padding ->
        when (val current = movieState) {
            is DetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.25F).clip(CircleShape)
                    )
                }
            }
            is DetailState.Success -> DetailSuccess(
                current.data,
                component.trending,
                padding,
                listState
            )
            is DetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    PlatformText(text = "Error loading details")

                    LaunchedEffect(current) {
                        Napier.e("Movie details error", current.exception)
                    }
                }
            }
        }
    }
}