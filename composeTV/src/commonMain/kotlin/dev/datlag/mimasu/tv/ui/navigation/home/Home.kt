package dev.datlag.mimasu.tv.ui.navigation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.datlag.mimasu.tv.ui.navigation.home.components.ShowCard
import dev.datlag.mimasu.ui.viewmodel.TrendingViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel

@Composable
fun Home() {
    val trendingViewModel = kodeinViewModel<TrendingViewModel>()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.statusBars.asPaddingValues()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val shows = trendingViewModel.tv.collectAsLazyPagingItems()

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Series",
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(shows.itemCount) { index ->
                        val show = shows[index]

                        ShowCard(show)
                    }
                }
            }
        }
    }
}