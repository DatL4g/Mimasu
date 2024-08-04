package dev.datlag.mimasu.tv.screen.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Carousel
import androidx.tv.material3.CompactCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tv.LocalTVPadding
import dev.datlag.mimasu.tv.common.ifElse
import dev.datlag.mimasu.tv.common.immersiveListGradient
import dev.datlag.mimasu.tv.common.requestFocusOnFirstGainingVisibility
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
actual fun TvHomeScreen(component: TvHomeComponent) {
    val trendingMovies by component.trendingMovies.map {
        it?.results?.mapNotNull { m -> m as? Trending.Response.Media.Movie }
    }.collectAsStateWithLifecycle(
        initialValue = null
    )
    var selectedCard by remember(trendingMovies) { mutableStateOf(trendingMovies?.firstOrNull()) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = selectedCard?.backdropPicture,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopEnd),
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.TopEnd,
            error = rememberAsyncImagePainter(
                model = selectedCard?.backdropPictureW500,
                contentScale = ContentScale.FillHeight
            )
        )

        Box(
            modifier = Modifier.fillMaxSize().immersiveListGradient(),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(LocalTVPadding.current)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(fraction = 0.6F)
                    .wrapContentHeight()
            ) {
                selectedCard?.alternativeTitle?.let { t ->
                    Text(
                        text = t,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                    )
                }


                Text(
                    text = selectedCard?.title ?: "Movie",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = selectedCard?.overview ?: "Description",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                )
            }
        }

        val firstChildRequester = remember {
            FocusRequester()
        }

        LazyRow(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 20.dp)
                .focusRestorer { firstChildRequester },
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(trendingMovies ?: emptyList()) {index, card ->
                CompactCard(
                    modifier = Modifier
                        .width(196.dp)
                        .aspectRatio(16F / 9)
                        .ifElse(index == 0, Modifier.focusRequester(firstChildRequester))
                        .onFocusChanged {
                            if (it.isFocused) {
                                selectedCard = card
                            }
                        },
                    onClick = { },
                    image = {
                        AsyncImage(
                            model = card.backdropPicture,
                            contentDescription = card.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds,
                            error = rememberAsyncImagePainter(
                                model = card.backdropPictureW500,
                                contentScale = ContentScale.FillBounds
                            )
                        )
                    },
                    title = { },
                    colors = CardDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }
}