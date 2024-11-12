package dev.datlag.mimasu.ui.navigation.screen.movie

import VideoPlayer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import dev.datlag.mimasu.ui.navigation.screen.movie.component.MovieToolbar
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun MovieScreen(component: MovieComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val movie by component.movie.collectAsStateWithLifecycle(null)
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MovieToolbar(
                modifier = Modifier.hazeChild(
                    listState = listState
                ),
                appBarState = appBarState,
                scrollBehavior = scrollBehavior,
                title = movie?.title?.ifBlank { null } ?: component.trending.title,
                subTitle = movie?.alternativeTitle?.ifBlank { null } ?: component.trending.alternativeTitle,
                backdrop = movie?.backdropPicture?.ifBlank { null } ?: component.trending.backdropPicture,
                fallbackBackdrop = movie?.backdropPictureW500?.ifBlank { null } ?: component.trending.backdropPictureW500,
                onBack = component::back
            )
        }
    ) {
        val trailer = remember(movie) { movie.youtubeTrailer() }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .haze(state = LocalHaze.current),
            contentPadding = it,
            state = listState
        ) {
            item {
                AsyncImage(
                    modifier = Modifier
                        .width(140.dp)
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    model = component.trending.posterPicture,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = component.trending.posterPictureW500,
                        contentScale = ContentScale.Crop
                    )
                )
            }
            item {
                Text(
                    text = "Description"
                )
            }
            item {
                Text(
                    text = component.trending.overview ?: component.trending.alternativeTitle ?: "NaN"
                )
            }
            if (trailer != null) {
                item {
                    Text(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 8.dp),
                        text = "Trailer",
                        style = Platform.typography().headlineSmall
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 8.dp)
                            .aspectRatio(16F/9F)
                            .clip(MaterialTheme.shapes.medium),
                    ) {
                        VideoPlayer(
                            modifier = Modifier.fillMaxSize(),
                            url = "https://youtube.com/watch?v=${trailer.key}"
                        )
                    }
                }
            }
        }
    }
}