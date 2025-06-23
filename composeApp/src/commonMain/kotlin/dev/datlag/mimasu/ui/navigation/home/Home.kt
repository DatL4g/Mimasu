package dev.datlag.mimasu.ui.navigation.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.home_movies
import dev.datlag.mimasu.composeapp.generated.resources.home_people
import dev.datlag.mimasu.composeapp.generated.resources.home_series
import dev.datlag.mimasu.composeapp.generated.resources.home_today
import dev.datlag.mimasu.composeapp.generated.resources.home_trending
import dev.datlag.mimasu.composeapp.generated.resources.home_week
import dev.datlag.mimasu.composeapp.generated.resources.home_your_movies
import dev.datlag.mimasu.composeapp.generated.resources.home_your_series
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import dev.datlag.mimasu.ui.ads.BannerAd
import dev.datlag.mimasu.ui.collectAsLazyPagingItems
import dev.datlag.mimasu.ui.custom.MovieCard
import dev.datlag.mimasu.ui.custom.MoviePager
import dev.datlag.mimasu.ui.custom.PagerWormIndicator
import dev.datlag.mimasu.ui.custom.ShowCard
import dev.datlag.mimasu.ui.custom.PersonCard
import dev.datlag.mimasu.ui.custom.ShowPager
import dev.datlag.mimasu.ui.navigation.home.components.AccountVerification
import dev.datlag.mimasu.ui.navigation.home.components.ExtensionUpdate
import dev.datlag.mimasu.ui.navigation.home.components.TimeWindowSelection
import dev.datlag.mimasu.ui.viewmodel.FirebaseViewModel
import dev.datlag.mimasu.ui.viewmodel.TrendingViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import org.jetbrains.compose.resources.stringResource

@Composable
fun Home(
    onPersonClicked: (People) -> Unit,
    onShowClicked: (TV) -> Unit,
    onMovieClicked: (Movie) -> Unit,
    onLogout: () -> Unit
) {
    val firebaseViewModel = kodeinViewModel<FirebaseViewModel>()
    val trendingViewModel = kodeinViewModel<TrendingViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.statusBars.asPaddingValues()
    ) {
        item {
            ExtensionUpdate(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp)
            )
        }
        item {
            AccountVerification(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                onLogout = onLogout
            )
        }
        item {
            val hasBookmarks by firebaseViewModel.hasBookmarkedShows.collectAsStateWithLifecycle(false)

            if (hasBookmarks) {
                Column(
                    modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val bookmarked = firebaseViewModel.bookmarkedShows.collectAsLazyPagingItems()

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(Res.string.home_your_series),
                        style = Platform.typography().headlineSmall,
                        maxLines = 1
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(bookmarked.itemCount) { index ->
                            val show = bookmarked[index]

                            ShowCard(show) {
                                onShowClicked(it.asCommon())
                            }
                        }
                        when {
                            bookmarked.loadState.refresh is LoadState.Loading -> {
                                items(5) {
                                    ShowCard(show = null)
                                }
                            }
                            bookmarked.loadState.append is LoadState.Loading -> {
                                items(3) {
                                    ShowCard(show = null)
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            val hasBookmarks by firebaseViewModel.hasBookmarkedMovies.collectAsStateWithLifecycle(false)

            if (hasBookmarks) {
                Column(
                    modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val bookmarked = firebaseViewModel.bookmarkedMovies.collectAsLazyPagingItems()

                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(Res.string.home_your_movies),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(bookmarked.itemCount) { index ->
                            val movie = bookmarked[index]

                            MovieCard(
                                detailed = movie
                            ) {
                                onMovieClicked(it.asCommon())
                            }
                        }
                        when {
                            bookmarked.loadState.refresh is LoadState.Loading -> {
                                items(5) {
                                    MovieCard(detailed = null)
                                }
                            }
                            bookmarked.loadState.append is LoadState.Loading -> {
                                items(3) {
                                    MovieCard(detailed = null)
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            Text(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                text = stringResource(Res.string.home_trending),
                style = Platform.typography().headlineLarge,
                maxLines = 1
            )
        }
        item {
            val dayWeek by trendingViewModel.timeWindow.collectAsStateWithLifecycle()

            TimeWindowSelection(
                modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                selected = dayWeek,
                selectDay = {
                    trendingViewModel.updateToDayTimeWindow()
                },
                selectWeek = {
                    trendingViewModel.updateToWeekTimeWindow()
                }
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val people = trendingViewModel.people.collectAsLazyPagingItems()

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.home_people),
                    style = Platform.typography().headlineSmall,
                    maxLines = 1
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(people.itemCount) { index ->
                        val person = people[index]

                        PersonCard(person, placeholder = false, onPersonClicked)
                    }
                    when {
                        people.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                PersonCard(null)
                            }
                        }
                        people.loadState.append is LoadState.Loading -> {
                            items(3) {
                                PersonCard(null)
                            }
                        }
                    }
                }
            }
        }
        item {
            BannerAd(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp)
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val series = trendingViewModel.tv.collectAsLazyPagingItems()

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.home_series),
                    style = Platform.typography().headlineSmall,
                    maxLines = 1
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(series.itemCount) { index ->
                        val show = series[index]

                        ShowCard(show) {
                            onShowClicked(it)
                        }
                    }
                    when {
                        series.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                ShowCard(tv = null)
                            }
                        }
                        series.loadState.append is LoadState.Loading -> {
                            items(3) {
                                ShowCard(tv = null)
                            }
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val movies = trendingViewModel.movies.collectAsLazyPagingItems()

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(Res.string.home_movies),
                    style = Platform.typography().headlineSmall,
                    maxLines = 1
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(movies.itemCount) { index ->
                        val movie = movies[index]

                        MovieCard(
                            movie = movie
                        ) {
                            onMovieClicked(it)
                        }
                    }
                    when {
                        movies.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                MovieCard(movie = null)
                            }
                        }
                        movies.loadState.append is LoadState.Loading -> {
                            items(3) {
                                MovieCard(movie = null)
                            }
                        }
                    }
                }
            }
        }
    }
}
