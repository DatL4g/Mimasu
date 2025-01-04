package dev.datlag.mimasu.ui.navigation.screen.movie.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformCard
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.toImmutableList
import mimasu.composeapp.generated.resources.Res
import mimasu.composeapp.generated.resources.movie_about
import mimasu.composeapp.generated.resources.movie_characters
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterSection(
    characters: Collection<Details.Movie.Credits.Cast>?,
    modifier: Modifier = Modifier
) {
    if (!characters.isNullOrEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlatformText(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                text = stringResource(Res.string.movie_characters),
                style = Platform.typography().headlineSmall,
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(characters.toImmutableList(), key = { it.id }) { char ->
                    PlatformCard(
                        modifier = Modifier.width(96.dp).height(192.dp),
                        onClick = {

                        }
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxWidth()
                                .aspectRatio(0.7F)
                                .clip(Platform.shapes().medium),
                            model = char.profilePicture,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            error = rememberAsyncImagePainter(
                                model = char.profilePictureW500,
                                contentScale = ContentScale.Crop
                            )
                        )
                        PlatformText(
                            text = char.character?.ifBlank { null } ?: char.name?.ifBlank { null } ?: char.originalName?.ifBlank { null } ?: "",
                            style = Platform.typography().labelLarge,
                            maxLines = 2,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1F).fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}