package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.formatCurrencyShort
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_budget
import dev.datlag.mimasu.composeapp.generated.resources.movie_rating
import dev.datlag.mimasu.composeapp.generated.resources.movie_rating_placeholder
import dev.datlag.mimasu.composeapp.generated.resources.movie_revenue
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlin.math.roundToInt

@Composable
fun MovieInfo(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        movie.budget.takeIf { it > 0 }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.movie_budget),
                    style = Platform.typography().labelSmall
                )
                Text(
                    text = it.formatCurrencyShort(),
                    style = Platform.typography().titleLarge
                )
            }
        }
        movie.voteAverage.takeIf { it > 0F }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.movie_rating),
                    style = Platform.typography().labelSmall
                )
                Text(
                    text = stringResource(Res.string.movie_rating_placeholder, (it * 10F).roundToInt()),
                    style = Platform.typography().titleLarge
                )
            }
        }
        movie.revenue.takeIf { it > 0F }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.movie_revenue),
                    style = Platform.typography().labelSmall
                )
                Text(
                    text = it.formatCurrencyShort(),
                    style = Platform.typography().titleLarge
                )
            }
        }
    }
}