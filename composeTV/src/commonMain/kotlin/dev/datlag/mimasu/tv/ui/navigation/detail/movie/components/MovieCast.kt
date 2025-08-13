package dev.datlag.mimasu.tv.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClassicCard
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_movie_cast
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MovieCast(
    casting: SerializableImmutableSet<Movie.Credits.Cast>,
    modifier: Modifier = Modifier,
) {
    if (casting.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                text = stringResource(Res.string.tv_movie_cast),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(casting.toImmutableList()) { cast ->
                    ClassicCard(
                        modifier = Modifier.width(100.dp).height(200.dp),
                        onClick = { },
                        image = {
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

                            AsyncImage(
                                modifier = Modifier.fillMaxWidth().aspectRatio(0.7F),
                                model = logos.firstOrNull(),
                                contentScale = ContentScale.Crop,
                                error = rememberNestedImagePainter(
                                    models = logos.drop(1),
                                    contentScale = ContentScale.Crop,
                                ),
                                contentDescription = cast.character ?: cast.name
                            )
                        },
                        title = {
                            val name = cast.character?.ifBlank { null } ?: cast.name

                            Box(
                                modifier = Modifier.weight(1F).fillMaxWidth().padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}