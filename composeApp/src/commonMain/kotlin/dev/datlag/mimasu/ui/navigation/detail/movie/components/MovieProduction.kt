package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_cast
import dev.datlag.mimasu.composeapp.generated.resources.movie_companies
import dev.datlag.mimasu.composeapp.generated.resources.movie_countries
import dev.datlag.mimasu.composeapp.generated.resources.movie_crew
import dev.datlag.mimasu.composeapp.generated.resources.movie_production
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieProduction(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    val countries = remember(movie) { movie.productionCountries.mapNotNull {
        it.name?.ifBlank { null }
    }.toImmutableList() }
    // Keep company to search by id sometime
    val companies = remember(movie) { movie.productionCompanies.toImmutableList() }

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
                        ElevatedSuggestionChip(
                            onClick = { },
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