package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes

@Composable
fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        var loading by remember(movie.id) { mutableStateOf(true) }

        AsyncImage(
            modifier = Modifier
                .size(width = 100.dp, height = 160.dp)
                .clip(Platform.shapes().medium)
                .placeholder(
                    visible = loading,
                    shape = Platform.shapes().medium,
                    highlight = PlaceholderHighlight.fade()
                ),
            model = movie.poster,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = movie.posterW500,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = movie.posterW400,
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = movie.posterW300,
                        contentScale = ContentScale.Crop,
                        error = rememberAsyncImagePainter(
                            model = movie.posterW200,
                            contentScale = ContentScale.Crop,
                            error = rememberAsyncImagePainter(
                                model = movie.posterSource,
                                contentScale = ContentScale.Crop
                            )
                        )
                    )
                )
            ),
            contentDescription = movie.title,
            onLoading = {
                loading = true
            },
            onError = {
                loading = true
            },
            onSuccess = {
                loading = false
            }
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = movie.title,
            maxLines = 2,
            textAlign = TextAlign.Center,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
    }
}