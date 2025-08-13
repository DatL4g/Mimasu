package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_overview
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
fun MovieOverview(
    movie: Movie,
    initial: CommonMovie?,
    modifier: Modifier = Modifier
) {
    val overview = remember(movie.id, initial?.id) {
        movie.overview?.ifBlank { null } ?: initial?.overview?.ifBlank { null }
    }

    if (!overview.isNullOrBlank()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var expanded by rememberSaveable(overview) { mutableStateOf(false) }
            var expandable by remember(overview) { mutableStateOf(false) }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.movie_overview),
                style = Platform.typography().headlineSmall
            )
            SelectionContainer {
                Text(
                    text = overview,
                    softWrap = true,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                    onTextLayout = { result ->
                        if (!expanded) {
                            expandable = result.hasVisualOverflow
                        }
                    }
                )
            }
            if (expandable) {
                IconButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    MaterialSymbols(
                        name = if (expanded) MaterialSymbols.KEYBOARD_ARROW_UP else MaterialSymbols.KEYBOARD_ARROW_DOWN,
                        contentDescription = null
                    )
                }
            }
        }
    }
}