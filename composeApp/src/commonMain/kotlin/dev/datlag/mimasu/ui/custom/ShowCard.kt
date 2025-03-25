package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes

@Composable
fun ShowCard(show: TV) {
    Column(
        modifier = Modifier.width(100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var loading by remember(show.id) { mutableStateOf(true) }

        AsyncImage(
            modifier = Modifier
                .size(width = 100.dp, height = 160.dp)
                .clip(Platform.shapes().medium)
                .placeholder(
                    visible = loading,
                    shape = Platform.shapes().medium,
                    highlight = PlaceholderHighlight.fade()
                ),
            model = show.poster,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = show.posterW500,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = show.posterW400,
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = show.posterW300,
                        contentScale = ContentScale.Crop,
                        error = rememberAsyncImagePainter(
                            model = show.posterW200,
                            contentScale = ContentScale.Crop,
                            error = rememberAsyncImagePainter(
                                model = show.posterSource,
                                contentScale = ContentScale.Crop
                            )
                        )
                    )
                )
            ),
            contentDescription = show.name,
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
            text = show.name,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}