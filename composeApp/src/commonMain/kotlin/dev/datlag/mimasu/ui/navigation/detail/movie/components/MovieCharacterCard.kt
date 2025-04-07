package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
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
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MaterialSymbols.invoke
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MovieCharacterCard(
    cast: Movie.Credits.Cast,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        var loading by remember(cast.id) { mutableStateOf(true) }
        var fallback by remember(cast.id) { mutableStateOf(false) }
        val logos = remember(cast.id) {
            listOfNotNull(
                cast.logo,
                cast.logoW500,
                cast.logoW400,
                cast.logoW300,
                cast.logoW200,
                cast.logoSource
            ).toImmutableList()
        }
        val imageModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7F)

        if (fallback) {
            Surface(
                modifier = imageModifier
            ) {
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialSymbols(
                        modifier = Modifier.size(50.dp),
                        cast = cast,
                        contentDescription = cast.name,
                        filled = true
                    )
                }
            }
        } else {
            AsyncImage(
                modifier = imageModifier,
                model = logos.firstOrNull(),
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = logos.drop(1),
                    contentScale = ContentScale.Crop
                ),
                alignment = Alignment.Center,
                contentDescription = cast.name,
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

        Column(
            modifier = Modifier.weight(1F).padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            cast.character?.ifBlank { null }?.let {
                Text(
                    text = it,
                    style = Platform.typography().labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
            Text(
                text = cast.name,
                style = Platform.typography().labelLarge,
                maxLines = if (cast.character.isNullOrBlank()) 2 else 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}