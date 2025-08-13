package dev.datlag.mimasu.tv.ui.navigation.series

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_series_airing_today
import dev.datlag.mimasu.tv.tv_series_on_the_air
import dev.datlag.mimasu.tv.tv_series_popular
import dev.datlag.mimasu.tv.tv_series_top_rated
import dev.datlag.mimasu.tv.ui.custom.SeriesSection
import dev.datlag.mimasu.ui.common.plus
import dev.datlag.mimasu.ui.viewmodel.TvSeriesListsViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import io.tolgee.stringResource

@Composable
fun Series(
    paddingValues: PaddingValues,
    onShowClicked: (TV) -> Unit
) {
    val seriesViewModel = kodeinViewModel<TvSeriesListsViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues.plus(PaddingValues(top = 32.dp))
    ) {
        item {
            SeriesSection(
                flow = seriesViewModel.airingToday,
                title = stringResource(Res.string.tv_series_airing_today),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onShowClicked
            )
        }
        item {
            SeriesSection(
                flow = seriesViewModel.topRated,
                title = stringResource(Res.string.tv_series_top_rated),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onShowClicked
            )
        }
        item {
            SeriesSection(
                flow = seriesViewModel.onTheAir,
                title = stringResource(Res.string.tv_series_on_the_air),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onShowClicked
            )
        }
        item {
            SeriesSection(
                flow = seriesViewModel.popular,
                title = stringResource(Res.string.tv_series_popular),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                onClick = onShowClicked
            )
        }
    }
}