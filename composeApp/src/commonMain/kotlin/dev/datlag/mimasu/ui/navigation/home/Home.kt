package dev.datlag.mimasu.ui.navigation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import dev.datlag.mimasu.ui.collectAsLazyPagingItems
import dev.datlag.mimasu.ui.viewmodel.TrendingViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography

@Composable
fun Home() {
    val trendingViewModel = kodeinViewModel<TrendingViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dayWeek by trendingViewModel.timeWindow.collectAsStateWithLifecycle()

                Text(
                    modifier = Modifier.weight(1F),
                    text = "Trending",
                    fontWeight = FontWeight.Bold,
                    style = Platform.typography().headlineLarge
                )
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = dayWeek is TimeWindow.Day,
                        onClick = {
                            trendingViewModel.updateToDayTimeWindow()
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 0,
                            count = 2
                        ),
                        label = {
                            Text(
                                text = "Today",
                                maxLines = 1,
                                style = Platform.typography().labelSmall
                            )
                        }
                    )
                    SegmentedButton(
                        selected = dayWeek is TimeWindow.Week,
                        onClick = {
                            trendingViewModel.updateToWeekTimeWindow()
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 1,
                            count = 2
                        ),
                        label = {
                            Text(
                                text = "Week",
                                maxLines = 1,
                                style = Platform.typography().labelSmall
                            )
                        }
                    )
                }
            }
        }
        item {
            Column(
                modifier = Modifier.fillParentMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val people = trendingViewModel.people.collectAsLazyPagingItems()

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "People",
                    style = Platform.typography().headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(people.itemCount) { index ->
                        val person = people[index]

                        if (person != null) {
                            PersonCard(person)
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.fillParentMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val series = trendingViewModel.tv.collectAsLazyPagingItems()

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "TV Shows",
                    style = Platform.typography().headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(series.itemCount) { index ->
                        val show = series[index]

                        if (show != null) {
                            ShowCard(show)
                        }
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.fillParentMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val movies = trendingViewModel.movies.collectAsLazyPagingItems()

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Movies",
                    style = Platform.typography().headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(movies.itemCount) { index ->
                        val movie = movies[index]

                        if (movie != null) {
                            MovieCard(movie)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonCard(person: People) {
    Column(
        modifier = Modifier.width(100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier.size(100.dp).clip(CircleShape),
            model = person.logo,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = person.logoW500,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = person.logoSource,
                    contentScale = ContentScale.Crop
                )
            ),
            alignment = Alignment.Center,
            contentDescription = person.name
        )
        Text(
            text = person.name,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ShowCard(show: TV) {
    Column(
        modifier = Modifier.width(100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier.size(width = 100.dp, height = 140.dp).clip(Platform.shapes().medium),
            model = show.poster,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = show.posterW500,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = show.posterSource,
                    contentScale = ContentScale.Crop
                )
            ),
            contentDescription = show.name
        )
        Text(
            text = show.name,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MovieCard(movie: Movie) {
    Column(
        modifier = Modifier.width(100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier.size(width = 100.dp, height = 140.dp).clip(Platform.shapes().medium),
            model = movie.poster,
            contentScale = ContentScale.Crop,
            error = rememberAsyncImagePainter(
                model = movie.posterW500,
                contentScale = ContentScale.Crop,
                error = rememberAsyncImagePainter(
                    model = movie.posterSource,
                    contentScale = ContentScale.Crop
                )
            ),
            contentDescription = movie.title
        )
        Text(
            text = movie.title,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}