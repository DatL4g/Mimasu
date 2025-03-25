package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import dev.datlag.mimasu.tmdb.model.People

@Composable
fun PersonCard(person: People) {
    Column(
        modifier = Modifier.width(100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var loading by remember(person.id) { mutableStateOf(true) }

        AsyncImage(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .placeholder(
                    visible = loading,
                    shape = CircleShape,
                    highlight = PlaceholderHighlight.fade()
                ),
            model = person.logo,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = person.logoW500,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = person.logoSource,
                    contentScale = ContentScale.Crop
                )
            ),
            alignment = Alignment.Center,
            contentDescription = person.name,
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
            text = person.name,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}