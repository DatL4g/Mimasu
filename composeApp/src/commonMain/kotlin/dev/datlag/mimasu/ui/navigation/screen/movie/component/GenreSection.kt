package dev.datlag.mimasu.ui.navigation.screen.movie.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformBorder
import dev.datlag.tooling.compose.platform.PlatformClickableChipBorder
import dev.datlag.tooling.compose.platform.PlatformClickableChipColors
import dev.datlag.tooling.compose.platform.PlatformSuggestionChip
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import kotlinx.collections.immutable.toImmutableList

@Composable
fun GenreSection(
    genres: Collection<Details.Movie.Genre>,
    modifier: Modifier = Modifier
) {
    if (genres.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(genres.toImmutableList(), key = { g -> g.id }) { genre ->
                PlatformSuggestionChip(
                    onClick = { },
                    colors = PlatformClickableChipColors.suggestion(
                        contentColor = Platform.colorScheme().onPrimaryContainer,
                        containerColor = Platform.colorScheme().primaryContainer
                    ),
                    border = PlatformClickableChipBorder.suggestion(
                        border = PlatformBorder.None
                    ),
                    label = {
                        PlatformText(text = genre.name)
                    }
                )
            }
        }
    }
}