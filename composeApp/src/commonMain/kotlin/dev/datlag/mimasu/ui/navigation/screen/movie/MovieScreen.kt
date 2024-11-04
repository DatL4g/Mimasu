package dev.datlag.mimasu.ui.navigation.screen.movie

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.ui.navigation.screen.movie.component.MovieToolbar
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(component: MovieComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val movie by component.movie.collectAsStateWithLifecycle(null)

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MovieToolbar(
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
        LazyColumn(
            contentPadding = it
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
        }
    }
}