package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.show_seasons
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowGenres
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowInfo
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowOverview
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowPosterContent
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowProduction
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowContent(
    hazeState: HazeState,
    listState: LazyListState,
    show: Show,
    initial: TV?,
    padding: PaddingValues,

) {
    var selectedSeason by remember { mutableStateOf<Show.Season?>(null) }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .hazeSource(state = hazeState),
        contentPadding = padding
    ) {
        item {
            ShowPosterContent(
                show = show,
                initial = initial,
                season = selectedSeason,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(16.dp)
            )
        }
        item {
            ShowInfo(
                show = show,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            ShowGenres(
                show = show,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(2F),
                    onClick = {

                    },
                    shape = CircleShape.copy(topEnd = CornerSize(2.dp), bottomEnd = CornerSize(2.dp))
                ) {
                    MaterialSymbols(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        name = MaterialSymbols.STEPPERS,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.show_seasons))
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1F).padding(start = 4.dp),
                    onClick = {
                        selectedSeason = if (selectedSeason == null) {
                            show.seasons.firstOrNull()
                        } else {
                            show.seasons.elementAtOrNull(show.seasons.indexOf(selectedSeason).plus(1))
                        }
                    },
                    shape = CircleShape.copy(topStart = CornerSize(2.dp), bottomStart = CornerSize(2.dp))
                ) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    MaterialSymbols(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        name = MaterialSymbols.CHEVRON_RIGHT,
                        contentDescription = null
                    )
                }
            }
        }
        item {
            ShowOverview(
                show = show,
                initial = initial,
                season = selectedSeason,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            ShowProduction(
                show = show,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}