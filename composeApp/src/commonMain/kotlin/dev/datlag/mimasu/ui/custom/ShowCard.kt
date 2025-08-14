package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.common.rememberResolvedKanaTitles
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes

@Composable
fun ShowCard(
    tv: TV?,
    onClick: (TV) -> Unit = { }
) {
    val (romajiTitle, originalTitle) = rememberResolvedKanaTitles(tv)

    ShowCard(
        onClick = { tv?.let(onClick) },
        placeholder = tv == null,
        id = tv?.id,
        posters = tv.posters(fallbackShow = null),
        name = romajiTitle ?: tv?.name,
        originalName = originalTitle ?: tv?.originalName,
    )
}

@Composable
fun ShowCard(
    show: Show?,
    onClick: (Show) -> Unit = { }
) {
    val (romajiTitle, originalTitle) = rememberResolvedKanaTitles(show)

    ShowCard(
        onClick = { show?.let(onClick) },
        placeholder = show == null,
        id = show?.id,
        posters = show.posters(fallbackShow = null),
        name = romajiTitle ?: show?.name,
        originalName = originalTitle ?: show?.originalName,
    )
}

@Composable
private fun ShowCard(
    placeholder: Boolean,
    id: Int?,
    posters: Collection<String>,
    name: String?,
    originalName: String?,
    onClick: () -> Unit = { }
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(100.dp).height(220.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        var loading by remember(id) { mutableStateOf(true) }

        AsyncImage(
            modifier = Modifier
                .size(width = 100.dp, height = 160.dp)
                .align(Alignment.CenterHorizontally)
                .clip(Platform.shapes().medium)
                .placeholder(
                    visible = placeholder || loading,
                    shape = Platform.shapes().medium,
                    highlight = PlaceholderHighlight.fade()
                ),
            model = posters.firstOrNull(),
            contentScale = ContentScale.Crop,
            error = rememberNestedImagePainter(
                models = posters.drop(1),
                contentScale = ContentScale.Crop
            ),
            contentDescription = name,
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
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .placeholder(
                    visible = placeholder,
                    shape = Platform.shapes().small,
                    highlight = PlaceholderHighlight.fade()
                ),
            text = name ?: originalName ?: "",
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}