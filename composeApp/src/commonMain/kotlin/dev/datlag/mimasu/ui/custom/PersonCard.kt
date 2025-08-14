package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
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
import dev.datlag.mimasu.tmdb.common.logos
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.common.rememberResolvedKanaTitles
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes

@Composable
fun PersonCard(
    person: People?,
    placeholder: Boolean = person == null,
    onClick: (People) -> Unit = { }
) {
    Card(
        onClick = { person?.let(onClick) },
        modifier = Modifier.width(100.dp).height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        var loading by remember(person?.id) { mutableStateOf(true) }
        var fallback by remember(person?.id) { mutableStateOf(false) }
        val (romajiTitle, originalTitle) = rememberResolvedKanaTitles(person)

        if (fallback && !placeholder) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialSymbols(
                        modifier = Modifier.size(50.dp),
                        person = person,
                        contentDescription = person?.name,
                        filled = true
                    )
                }
            }
        } else {
            val logos = remember { person.logos() }

            AsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .placeholder(
                        visible = loading,
                        shape = CircleShape,
                        highlight = PlaceholderHighlight.fade()
                    ),
                model = logos.firstOrNull(),
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = logos.drop(1),
                    contentScale = ContentScale.Crop
                ),
                alignment = Alignment.Center,
                contentDescription = person?.name,
                onLoading = {
                    loading = true
                    fallback = false
                },
                onSuccess = {
                    loading = false
                    fallback = false
                },
                onError = {
                    fallback = true
                }
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .placeholder(
                    visible = person == null,
                    shape = Platform.shapes().small,
                    highlight = PlaceholderHighlight.fade()
                ),
            text = romajiTitle ?: person?.name ?: originalTitle ?: person?.originalName ?: "",
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}