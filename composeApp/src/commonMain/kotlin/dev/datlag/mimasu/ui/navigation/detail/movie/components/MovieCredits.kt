package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_cast
import dev.datlag.mimasu.composeapp.generated.resources.movie_crew
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun MovieCredits(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    val cast = remember(movie) { movie.credits?.cast.orEmpty().toImmutableList() }
    val crew = remember(movie) { movie.credits?.crew.orEmpty().toImmutableList() }
    val hasCast = remember(cast) { cast.isNotEmpty() }
    val hasCrew = remember(crew) { crew.isNotEmpty() }
    var displayCast by remember(movie, hasCast) { mutableStateOf(hasCast) }

    if (hasCast || hasCrew) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = if (displayCast) {
                        stringResource(Res.string.movie_cast)
                    } else {
                        stringResource(Res.string.movie_crew)
                    },
                    style = Platform.typography().headlineSmall,
                    maxLines = 1
                )
                if (hasCast && hasCrew) {
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = displayCast,
                            onClick = {
                                displayCast = true
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 0,
                                count = 2
                            ),
                            label = {
                                Text(
                                    text = stringResource(Res.string.movie_cast),
                                    maxLines = 1,
                                    style = Platform.typography().labelSmall
                                )
                            }
                        )
                        SegmentedButton(
                            selected = !displayCast,
                            onClick = {
                                displayCast = false
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 1,
                                count = 2
                            ),
                            label = {
                                Text(
                                    text = stringResource(Res.string.movie_crew),
                                    maxLines = 1,
                                    style = Platform.typography().labelSmall
                                )
                            }
                        )
                    }
                }
            }
            LazyRow(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                if (displayCast) {
                    items(cast) {
                        MovieCharacterCard(
                            cast = it,
                            modifier = Modifier.width(100.dp).height(200.dp),
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}