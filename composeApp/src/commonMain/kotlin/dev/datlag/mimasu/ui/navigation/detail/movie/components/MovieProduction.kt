package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_countries
import dev.datlag.mimasu.composeapp.generated.resources.movie_production
import dev.datlag.mimasu.tmdb.common.logos
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieProduction(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    val countries = remember(movie.id) { movie.productionCountries.mapNotNull {
        it.name?.ifBlank { null }
    }.toImmutableList() }
    // Keep company to search by id sometime
    val companies = remember(movie.id) { movie.productionCompanies.toImmutableList() }

    if (countries.isNotEmpty() || companies.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.movie_production),
                style = Platform.typography().headlineSmall,
                maxLines = 1
            )
            if (countries.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.GLOBE_LOCATION_PIN,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(Res.string.movie_countries),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = countries.joinToString())
                }
            }
            if (companies.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    companies.forEach {
                        val logos = remember(it.id) { it.logos().toImmutableList() }

                        ElevatedSuggestionChip(
                            onClick = { },
                            icon = if (logos.isEmpty()) null else {
                                {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(SuggestionChipDefaults.IconSize)
                                            .clip(CircleShape),
                                        model = logos.firstOrNull(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        error = rememberNestedImagePainter(
                                            models = logos.drop(1),
                                            contentScale = ContentScale.Fit
                                        )
                                    )
                                }
                            },
                            label = {
                                Text(text = it.name)
                            }
                        )
                    }
                }
            }
        }
    }
}