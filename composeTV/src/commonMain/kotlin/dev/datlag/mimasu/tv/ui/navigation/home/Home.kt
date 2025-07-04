package dev.datlag.mimasu.tv.ui.navigation.home

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.TabRow
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_home_trending_movies_today
import dev.datlag.mimasu.tv.tv_home_trending_movies_weekly
import dev.datlag.mimasu.tv.tv_home_trending_series_today
import dev.datlag.mimasu.tv.tv_home_trending_series_weekly
import dev.datlag.mimasu.tv.ui.navigation.home.components.MoviesSection
import dev.datlag.mimasu.tv.ui.navigation.home.components.SeriesSection
import dev.datlag.mimasu.ui.viewmodel.TrendingViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun Home() {
    val trendingViewModel = kodeinViewModel<TrendingViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.statusBars.asPaddingValues()
    ) {
        item {
            SeriesSection(
                flow = trendingViewModel.dayTV,
                title = stringResource(Res.string.tv_home_trending_series_today),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp)
            )
        }
        item {
            MoviesSection(
                flow = trendingViewModel.dayMovies,
                title = stringResource(Res.string.tv_home_trending_movies_today),
                orientation = Orientation.Horizontal,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp)
            )
        }
        item {
            SeriesSection(
                flow = trendingViewModel.weekTV,
                title = stringResource(Res.string.tv_home_trending_series_weekly),
                orientation = Orientation.Vertical,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp)
            )
        }
        item {
            MoviesSection(
                flow = trendingViewModel.weekMovies,
                title = stringResource(Res.string.tv_home_trending_movies_weekly),
                orientation = Orientation.Vertical,
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp)
            )
        }
    }
}