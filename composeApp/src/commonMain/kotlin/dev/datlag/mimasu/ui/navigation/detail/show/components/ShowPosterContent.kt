package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.show_air_date_format
import dev.datlag.mimasu.composeapp.generated.resources.show_status_canceled
import dev.datlag.mimasu.composeapp.generated.resources.show_status_ended
import dev.datlag.mimasu.composeapp.generated.resources.show_status_in_production
import dev.datlag.mimasu.composeapp.generated.resources.show_status_pilot
import dev.datlag.mimasu.composeapp.generated.resources.show_status_planned
import dev.datlag.mimasu.composeapp.generated.resources.show_status_returning
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.common.formatMedium
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.setFrom
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ShowPosterContent(
    show: Show,
    initial: TV?,
    season: Show.Season?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val seasonPosters = remember(season) { season?.posters(fallbackShow = null).orEmpty() }
        val showPosters = remember(show.id) { show.posters(initial) }
        val posters = remember(seasonPosters, showPosters) {
            setFrom(seasonPosters, showPosters).toImmutableList()
        }
        var currentPainter by remember(show.id) { mutableStateOf<Painter?>(null) }

        AsyncImage(
            modifier = Modifier
                .width(140.dp)
                .height(200.dp)
                .clip(Platform.shapes().medium),
            model = posters.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = currentPainter,
            error = rememberNestedImagePainter(
                models = posters.drop(1),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    currentPainter = it.painter
                }
            ),
            onSuccess = {
                currentPainter = it.painter
            }
        )
        Column(
            modifier = Modifier.weight(1F).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            val tagline = remember(show.id) {
                show.tagline?.ifBlank { null } ?: show.originalTagline?.ifBlank { null }
            }

            tagline?.let {
                Text(text = it)
                Spacer(modifier = Modifier.weight(1F))
            }
            show.firstAirLocalDate.formatMedium(
                fallbackFormat = Res.string.show_air_date_format,
                fallbackValue = show.firstAirDate
            )?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.TODAY,
                        contentDescription = null
                    )
                    Text(
                        text = it,
                        maxLines = 1
                    )
                }
            }
            show.lastAirLocalDate.formatMedium(
                fallbackFormat = Res.string.show_air_date_format,
                fallbackValue = show.lastAirDate
            )?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MaterialSymbols(
                        name = if (show.inProduction) {
                            MaterialSymbols.DATE_RANGE
                        } else {
                            MaterialSymbols.EVENT
                        },
                        contentDescription = null
                    )
                    Text(
                        text = it,
                        maxLines = 1
                    )
                }
            }
            show.runtimeAverage.takeIf { it > 0 }?.let {
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
            show.status?.ifBlank { null }?.let {
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
                            is Show.Status.Returning -> stringResource(Res.string.show_status_returning)
                            is Show.Status.Planned -> stringResource(Res.string.show_status_planned)
                            is Show.Status.Pilot -> stringResource(Res.string.show_status_pilot)
                            is Show.Status.InProduction -> stringResource(Res.string.show_status_in_production)
                            is Show.Status.Ended -> stringResource(Res.string.show_status_ended)
                            is Show.Status.Canceled -> stringResource(Res.string.show_status_canceled)
                            else -> it.toString()
                        },
                        maxLines = 1
                    )
                }
            }
        }
    }
}