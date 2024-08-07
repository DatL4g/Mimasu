package dev.datlag.mimasu.ui.navigation.screen.initial.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.tooling.compose.platform.PlatformCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val trendingMovies = component.trendingMovies.collectAsLazyPagingItems()
    val trendingPeople = component.trendingPeople.collectAsLazyPagingItems()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickyHeader {
            Text(text = "Trending Movies")
        }
        item {
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(trendingMovies.itemCount) { index ->
                    PlatformCard(
                        modifier = Modifier
                            .width(140.dp)
                            .height(200.dp),
                        onClick = { }
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = trendingMovies[index]!!.posterPicture,
                            contentDescription = trendingMovies[index]?.title,
                            contentScale = ContentScale.Crop,
                            error = rememberAsyncImagePainter(
                                model = trendingMovies[index]!!.posterPictureW500,
                                contentScale = ContentScale.Crop
                            )
                        )
                    }
                }
            }
        }
        items(
            trendingMovies.itemCount
        ) { index ->
            Text(text = trendingMovies[index]!!.title)
        }
        stickyHeader {
            Text(text = "People only")
        }
        items(trendingPeople.itemCount) { index ->
            Text(text = trendingPeople[index]!!.name)
        }
    }
}