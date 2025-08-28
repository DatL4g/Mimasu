package dev.datlag.mimasu.ui.navigation.series

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.series_airing_today
import dev.datlag.mimasu.composeapp.generated.resources.series_on_the_air
import dev.datlag.mimasu.composeapp.generated.resources.series_popular
import dev.datlag.mimasu.composeapp.generated.resources.series_top_rated
import dev.datlag.mimasu.tmdb.model.TV
import androidx.paging.compose.collectAsLazyPagingItems
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.ScrollIconButton
import dev.datlag.mimasu.ui.custom.ShowCard
import dev.datlag.mimasu.ui.viewmodel.TvSeriesListsViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@Composable
fun Series(
    onSeriesClicked: (TV) -> Unit
) {
    val seriesListsViewModel = kodeinViewModel<TvSeriesListsViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = WindowInsets.statusBars.asPaddingValues()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val airingToday = seriesListsViewModel.airingToday.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.series_airing_today),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = airingToday.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = airingToday.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(airingToday.itemCount) { index ->
                        val series = airingToday[index]

                        ShowCard(series, onSeriesClicked)
                    }
                    when {
                        airingToday.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                ShowCard(tv = null)
                            }
                        }
                        airingToday.loadState.append is LoadState.Loading -> {
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
                val topRated = seriesListsViewModel.topRated.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.series_top_rated),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = topRated.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = topRated.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(topRated.itemCount) { index ->
                        val series = topRated[index]

                        ShowCard(series, onSeriesClicked)
                    }
                    when {
                        topRated.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                ShowCard(tv = null)
                            }
                        }
                        topRated.loadState.append is LoadState.Loading -> {
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
                val onTheAir = seriesListsViewModel.onTheAir.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.series_on_the_air),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )

                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = onTheAir.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = onTheAir.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(onTheAir.itemCount) { index ->
                        val series = onTheAir[index]

                        ShowCard(series, onSeriesClicked)
                    }
                    when {
                        onTheAir.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                ShowCard(tv = null)
                            }
                        }
                        onTheAir.loadState.append is LoadState.Loading -> {
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
                val popular = seriesListsViewModel.popular.collectAsLazyPagingItems()
                val listState = rememberLazyListState()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.series_popular),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    if (!Platform.isAndroid) {
                        Spacer(modifier = Modifier.weight(1F))
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.Start,
                            maxScrollItem = popular.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_LEFT,
                                contentDescription = null
                            )
                        }
                        ScrollIconButton(
                            listState = listState,
                            alignment = Alignment.End,
                            maxScrollItem = popular.itemCount
                        ) {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_ARROW_RIGHT,
                                contentDescription = null
                            )
                        }
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    state = listState
                ) {
                    items(popular.itemCount) { index ->
                        val series = popular[index]

                        ShowCard(series, onSeriesClicked)
                    }
                    when {
                        popular.loadState.refresh is LoadState.Loading -> {
                            items(5) {
                                ShowCard(tv = null)
                            }
                        }
                        popular.loadState.append is LoadState.Loading -> {
                            items(3) {
                                ShowCard(tv = null)
                            }
                        }
                    }
                }
            }
        }
    }
}