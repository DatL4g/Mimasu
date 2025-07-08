package dev.datlag.mimasu.tv.ui.navigation.detail.show.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.NavigationDrawerScope
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_show_episodes_count
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.pluralStringResource

@Composable
internal fun NavigationDrawerScope.ShowDrawerContent(
    show: Show?,
    season: Show.Season?,
    contentFocus: FocusRequester,
    modifier: Modifier = Modifier,
    onSelect: (Show.Season) -> Unit
) {
    val seasons = remember(show?.seasons) { show?.displaySeasons.orEmpty().toImmutableList() }
    var selectedSeason by remember(show?.id, season) { mutableStateOf(season) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        itemsIndexed(seasons) { index, s ->
            NavigationDrawerItem(
                modifier = Modifier.focusProperties {
                    end = contentFocus
                    if (index == seasons.size - 1) {
                        next = contentFocus
                        down = contentFocus
                    }
                },
                selected = s == selectedSeason,
                onClick = {
                    selectedSeason = s

                    if (s != season) {
                        onSelect(s)
                    }
                },
                leadingContent = {
                    if (s.seasonNumber <= 0) {
                        MaterialSymbols(
                            name = MaterialSymbols.STAR_SHINE,
                            contentDescription = null,
                            filled = selectedSeason == s
                        )
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = s.seasonNumber.toString(),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                content = {
                    Text(
                        text = s.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Text(
                        text = pluralStringResource(
                            Res.plurals.tv_show_episodes_count,
                            s.episodeCount,
                            s.episodeCount
                        ),
                        maxLines = 1
                    )
                }
            )
        }
    }
}