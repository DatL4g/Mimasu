package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ShowGenres(
    show: Show,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    val genres = remember(show.id) { show.genres.toImmutableList() }

    if (genres.isNotEmpty()) {
        val inBetweenSpace = remember(genres) {
            val value = when {
                genres.size <= 1 -> 16.dp
                genres.size >= 4 -> 8.dp
                else -> {
                    val step = (16F - 8F) / 3F
                    (16F - (step * (genres.size - 1))).dp
                }
            }

            max(min(value, 16.dp), 8.dp)
        }

        LazyRow(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(inBetweenSpace, Alignment.CenterHorizontally)
        ) {
            items(genres) { genre ->
                SuggestionChip(
                    onClick = {
                        onClick(genre.id)
                    },
                    label = {
                        Text(text = genre.name)
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Platform.colorScheme().secondaryContainer,
                        labelColor = Platform.colorScheme().onSecondaryContainer,
                        iconContentColor = Platform.colorScheme().onSecondaryContainer
                    ),
                    border = null
                )
            }
        }
    }
}