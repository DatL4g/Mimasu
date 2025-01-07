package dev.datlag.mimasu.ui.navigation.screen.movie.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.other.I18N
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.typography
import mimasu.composeapp.generated.resources.Res
import mimasu.composeapp.generated.resources.movie_score
import mimasu.composeapp.generated.resources.movie_score_placeholder
import mimasu.composeapp.generated.resources.movie_votes

@Composable
fun RatingSection(
    count: Int,
    score: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0 || score > 0) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally)
        ) {
            if (count > 0F) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlatformText(
                        text = I18N.stringResource(Res.string.movie_votes),
                        style = Platform.typography().labelSmall
                    )
                    PlatformText(
                        text = "$count",
                        style = Platform.typography().displaySmall
                    )
                }
            }
            if (score > 0) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlatformText(
                        text = I18N.stringResource(Res.string.movie_score),
                        style = Platform.typography().labelSmall
                    )
                    PlatformText(
                        text = I18N.stringResource(Res.string.movie_score_placeholder, score),
                        style = Platform.typography().displaySmall
                    )
                }
            }
        }
    }
}