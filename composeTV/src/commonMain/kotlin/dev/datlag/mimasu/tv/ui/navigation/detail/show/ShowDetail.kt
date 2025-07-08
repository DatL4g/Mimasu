package dev.datlag.mimasu.tv.ui.navigation.detail.show

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel

@Composable
internal fun ShowDetail() {
    val showViewModel = kodeinViewModel<ShowViewModel>()
    val showState by showViewModel.show.collectAsStateWithLifecycle(ShowViewModel.ShowState.Loading)
    val initial by showViewModel.initialShow.collectAsStateWithLifecycle()
    val showSeason by showViewModel.showSeason.collectAsStateWithLifecycle()
    val initialSeasonState = remember(showSeason) {
        if (showSeason == null) {
            ShowViewModel.SeasonState.Empty
        } else {
            ShowViewModel.SeasonState.Loading
        }
    }
    val seasonState by showViewModel.season.collectAsStateWithLifecycle(initialSeasonState)

    when (val current = showState) {
        is ShowViewModel.ShowState.Error -> {
            ErrorState(
                throwable = current.throwable,
                additionalInfo = "[TV] ShowDetail",
                modifier = Modifier.fillMaxSize()
            )
        }
        is ShowViewModel.ShowState.Loading, is ShowViewModel.ShowState.Success -> {
            ShowContent(
                show = current.getOrNull(),
                initial = initial,
                showSeason = showSeason,
                seasonState = seasonState,
                onSelectSeason = {
                    showViewModel.select(it)
                }
            )
        }
    }
}