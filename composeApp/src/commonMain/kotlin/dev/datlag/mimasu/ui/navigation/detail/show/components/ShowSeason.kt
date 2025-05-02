package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.toSize
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.show_season_next
import dev.datlag.mimasu.composeapp.generated.resources.show_season_placeholder
import dev.datlag.mimasu.composeapp.generated.resources.show_seasons
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowSeason(
    show: Show,
    season: Show.Season?,
    modifier: Modifier = Modifier,
    onSelect: (Show.Season) -> Unit
) {
    val seasons = remember(show.id, show.numberOfSeasons) { show.displaySeasons.toImmutableList() }

    if (seasons.isNotEmpty()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val next = remember(show.id, show.numberOfSeasons, season) {
                season?.let {
                    show.displaySeasons.elementAtOrNull(show.displaySeasons.indexOf(it) + 1)
                }
            }

            Button(
                modifier = Modifier
                    .weight(1F)
                    .animateContentSize(),
                onClick = {
                    show.displaySeasons.firstOrNull()?.let(onSelect)
                },
                shape = if (next == null) {
                    ButtonDefaults.shape
                } else {
                    CircleShape.copy(topEnd = CornerSize(2.dp), bottomEnd = CornerSize(2.dp))
                }
            ) {
                val name = remember(season) {
                    season?.name?.ifBlank { null }
                } ?: season?.name?.toIntOrNull()?.let { stringResource(Res.string.show_season_placeholder, it) }

                MaterialSymbols(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    name = MaterialSymbols.STEPPERS,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = name ?: stringResource(Res.string.show_seasons),
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (next != null) {
                FilledTonalButton(
                    onClick = {
                        onSelect(next)
                    },
                    shape = CircleShape.copy(topStart = CornerSize(2.dp), bottomStart = CornerSize(2.dp))
                ) {
                    Text(text = stringResource(Res.string.show_season_next))
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    MaterialSymbols(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        name = MaterialSymbols.CHEVRON_RIGHT,
                        contentDescription = null
                    )
                }
            }
        }
    }
}