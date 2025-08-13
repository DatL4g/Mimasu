package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_release_date_format
import dev.datlag.mimasu.composeapp.generated.resources.movie_status_canceled
import dev.datlag.mimasu.composeapp.generated.resources.movie_status_in_production
import dev.datlag.mimasu.composeapp.generated.resources.movie_status_planned
import dev.datlag.mimasu.composeapp.generated.resources.movie_status_post_production
import dev.datlag.mimasu.composeapp.generated.resources.movie_status_released
import dev.datlag.mimasu.composeapp.generated.resources.movie_status_rumored
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.common.formatMedium
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes
import io.tolgee.stringResource
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
fun MoviePosterContent(
    movie: Movie,
    initial: CommonMovie?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val posters = remember(movie.id) { movie.posters(initial) }

        AsyncImage(
            modifier = Modifier
                .width(140.dp)
                .height(200.dp)
                .clip(Platform.shapes().medium),
            model = posters.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            error = rememberNestedImagePainter(
                models = posters.drop(1),
                contentScale = ContentScale.Crop
            )
        )
        Column(
            modifier = Modifier.weight(1F).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            val tagline = remember(movie.id) {
                movie.tagline?.ifBlank { null } ?: movie.originalTagline?.ifBlank { null }
            }

            tagline?.let {
                Text(text = it)
                Spacer(modifier = Modifier.weight(1F))
            }
            movie.releaseLocalDate.formatMedium(
                fallbackFormat = Res.string.movie_release_date_format,
                fallbackValue = movie.releaseDate
            )?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.EVENT,
                        contentDescription = null
                    )
                    Text(
                        text = it,
                        maxLines = 1
                    )
                }
            }
            movie.runtime.takeIf { it > 0 }?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.SCHEDULE,
                        contentDescription = null
                    )
                    Text(
                        text = it.toDuration(DurationUnit.MINUTES).toString(),
                        maxLines = 1
                    )
                }
            }
            movie.status?.ifBlank { null }?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.RSS_FEED,
                        contentDescription = null
                    )
                    Text(
                        text = when (it) {
                            is Movie.Status.Rumored -> stringResource(Res.string.movie_status_rumored)
                            is Movie.Status.Planned -> stringResource(Res.string.movie_status_planned)
                            is Movie.Status.InProduction -> stringResource(Res.string.movie_status_in_production)
                            is Movie.Status.PostProduction -> stringResource(Res.string.movie_status_post_production)
                            is Movie.Status.Released -> stringResource(Res.string.movie_status_released)
                            is Movie.Status.Canceled -> stringResource(Res.string.movie_status_canceled)
                            else -> it.toString()
                        },
                        maxLines = 1
                    )
                }
            }
        }
    }
}