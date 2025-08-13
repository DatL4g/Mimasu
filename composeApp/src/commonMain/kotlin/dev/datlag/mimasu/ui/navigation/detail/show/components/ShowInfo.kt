package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.show_episodes
import dev.datlag.mimasu.composeapp.generated.resources.show_rating
import dev.datlag.mimasu.composeapp.generated.resources.show_rating_placeholder
import dev.datlag.mimasu.composeapp.generated.resources.show_seasons
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlin.math.roundToInt

@Composable
fun ShowInfo(
    show: Show,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        show.numberOfEpisodes.takeIf { it > 0 }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.show_episodes),
                    style = Platform.typography().labelSmall
                )
                Text(
                    text = it.toString(),
                    style = Platform.typography().titleLarge
                )
            }
        }
        show.numberOfSeasons.takeIf { it > 0 }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.show_seasons),
                    style = Platform.typography().labelSmall
                )
                Text(
                    text = it.toString(),
                    style = Platform.typography().titleLarge
                )
            }
        }
        show.voteAverage.takeIf { it > 0F }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.show_rating),
                    style = Platform.typography().labelSmall
                )
                Text(
                    text = stringResource(Res.string.show_rating_placeholder, (it * 10F).roundToInt()),
                    style = Platform.typography().titleLarge
                )
            }
        }
    }
}