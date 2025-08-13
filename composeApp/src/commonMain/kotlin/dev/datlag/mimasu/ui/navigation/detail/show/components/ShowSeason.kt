package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.show_season_next
import dev.datlag.mimasu.composeapp.generated.resources.show_season_placeholder
import dev.datlag.mimasu.composeapp.generated.resources.show_season_select_info
import dev.datlag.mimasu.composeapp.generated.resources.show_seasons
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowSeason(
    show: Show,
    season: Show.Season?,
    modifier: Modifier = Modifier,
    onSelect: (Show.Season) -> Unit
) {
    val seasons = remember(show.id, show.numberOfSeasons) { show.displaySeasons.toImmutableList() }

    if (seasons.size > 1) {
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
            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                var selectedSeason by remember(show.id, season) { mutableStateOf(season) }

                ModalBottomSheet(
                    onDismissRequest = {
                        showDialog = false
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().selectableGroup()
                    ) {
                        stickyHeader {
                            Row(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .background(BottomSheetDefaults.ContainerColor)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        showDialog = false
                                    }
                                ) {
                                    MaterialSymbols(
                                        name = MaterialSymbols.ARROW_BACK_IOS_NEW,
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    text = stringResource(Res.string.show_seasons),
                                    style = Platform.typography().titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        selectedSeason?.let(onSelect)
                                        showDialog = false
                                    },
                                    enabled = selectedSeason != null
                                ) {
                                    MaterialSymbols(
                                        name = MaterialSymbols.CHECK,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        item {
                            Text(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .padding(horizontal = 16.dp),
                                textAlign = TextAlign.Center,
                                text = stringResource(Res.string.show_season_select_info)
                            )
                        }
                        items(seasons) { s ->
                            val name = remember(s) {
                                s.name.ifBlank { null }
                            } ?: s.name.toIntOrNull()?.let { stringResource(Res.string.show_season_placeholder, it) }

                            Row(
                                modifier = Modifier
                                    .fillParentMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = s == selectedSeason,
                                        role = Role.RadioButton,
                                        onClick = {
                                            if (selectedSeason == s) {
                                                s.let(onSelect)
                                                showDialog = false
                                            } else {
                                                selectedSeason = s
                                            }
                                        }
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = s == selectedSeason,
                                    onClick = null
                                )
                                Text(
                                    text = name ?: s.name,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                modifier = Modifier
                    .weight(1F)
                    .animateContentSize(),
                onClick = {
                    showDialog = true
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