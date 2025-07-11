package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MovieCharacterCard(
    cast: Movie.Credits.Cast,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
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

    MovieCharacterCard(
        id = cast.id,
        logos = logos,
        character = cast.character,
        name = cast.name,
        fallbackIcon = {
            MaterialSymbols(
                modifier = Modifier.size(50.dp),
                cast = cast,
                contentDescription = cast.character ?: cast.name,
                filled = true
            )
        },
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun MovieCharacterCard(
    crew: Movie.Credits.Crew,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val logos = remember(crew.id) {
        listOfNotNull(
            crew.logo,
            crew.logoW500,
            crew.logoW400,
            crew.logoW300,
            crew.logoW200,
            crew.logoSource
        ).toImmutableList()
    }

    MovieCharacterCard(
        id = crew.id,
        logos = logos,
        character = null,
        name = crew.name,
        fallbackIcon = {
            MaterialSymbols(
                modifier = Modifier.size(50.dp),
                crew = crew,
                contentDescription = crew.name,
                filled = true
            )
        },
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun MovieCharacterCard(
    id: Int,
    logos: ImmutableList<String>,
    character: String?,
    name: String,
    fallbackIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        var loading by remember(id) { mutableStateOf(true) }
        var fallback by remember(id) { mutableStateOf(false) }
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
                    fallbackIcon()
                }
            }
        } else {
            AsyncImage(
                modifier = imageModifier,
                model = logos.firstOrNull(),
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = logos.drop(1),
                    contentScale = ContentScale.Crop,
                    onError = {
                        fallback = true
                    }
                ),
                alignment = Alignment.Center,
                contentDescription = character ?: name,
                onLoading = {
                    loading = true
                    fallback = false
                },
                onSuccess = {
                    loading = false
                    fallback = false
                }
            )
        }

        Column(
            modifier = Modifier.weight(1F).padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            character?.ifBlank { null }?.let {
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
                text = name,
                style = Platform.typography().labelLarge,
                maxLines = if (character.isNullOrBlank()) 2 else 1,
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