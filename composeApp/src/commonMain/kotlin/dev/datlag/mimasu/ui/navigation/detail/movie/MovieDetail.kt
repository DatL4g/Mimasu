package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetail(
    onBack: () -> Unit
) {
    val movieViewModel = kodeinViewModel<MovieViewModel>()
    val movie by movieViewModel.movie.collectAsStateWithLifecycle(null)
    val initial by movieViewModel.initialMovie.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth().matchParentSize(),
                    model = movie?.backdrop,
                    contentDescription = null,
                    error = rememberAsyncImagePainter(
                        model = movie?.backdropW500,
                        contentScale = ContentScale.Crop,
                        error = rememberAsyncImagePainter(
                            model = movie?.backdropSource,
                            contentScale = ContentScale.Crop,
                            error = rememberAsyncImagePainter(
                                model = initial?.backdrop,
                                contentScale = ContentScale.Crop,
                                error = rememberAsyncImagePainter(
                                    model = initial?.backdropW500,
                                    contentScale = ContentScale.Crop,
                                    error = rememberAsyncImagePainter(
                                        model = initial?.backdropSource,
                                        contentScale = ContentScale.Crop
                                    ),
                                ),
                            ),
                        ),
                    ),
                    contentScale = ContentScale.Crop
                )
                LargeTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                onBack()
                            }
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.ARROW_BACK_IOS_NEW,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(text = movie?.title ?: initial?.title ?: "")
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                )
            }
        }
    ) {

    }
}