package dev.datlag.mimasu.ui.navigation.screen.movie.component

import VideoPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.haze
import dev.datlag.mimasu.LocalHaze
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.common.youtubeTrailer
import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.ui.custom.component.IconText
import dev.datlag.mimasu.ui.theme.SchemeTheme
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformBorder
import dev.datlag.tooling.compose.platform.PlatformClickableChipBorder
import dev.datlag.tooling.compose.platform.PlatformClickableChipColors
import dev.datlag.tooling.compose.platform.PlatformSuggestionChip
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun DetailSuccess(
    movie: Details.Movie,
    trending: Trending.Response.Media.Movie?,
    padding: PaddingValues,
    listState: LazyListState,
    colorState: SchemeTheme.Updater<Painter>?
) {
    val trailer = remember(movie) { movie.youtubeTrailer() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .haze(state = LocalHaze.current),
        contentPadding = padding,
        state = listState
    ) {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .width(140.dp)
                        .height(200.dp)
                        .clip(Platform.shapes().medium)
                        .shadow(
                            elevation = 8.dp,
                            shape = Platform.shapes().medium,
                            spotColor = Platform.colorScheme().primary
                        ),
                    model = movie.posterPicture,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = rememberNestedImagePainter(
                        models = persistentSetOf(trending?.posterPicture, movie.posterPictureW500, trending?.posterPictureW500),
                        contentScale = ContentScale.Crop,
                        onSuccess = {
                            colorState?.updateFrom(it.painter)
                        }
                    ),
                    onSuccess = {
                        colorState?.updateFrom(it.painter)
                    }
                )
                Column(
                    modifier = Modifier.weight(1F).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                ) {
                    IconText(
                        icon = Icons.Rounded.Timer,
                        text = "${movie.runtime}min"
                    )
                    IconText(
                        icon = Icons.Rounded.AttachMoney,
                        text = "${movie.budget}"
                    )
                }
            }
        }
        item {
            RatingSection(
                count = movie.count,
                score = movie.averageScore,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
        item {
            GenreSection(
                genres = movie.genres,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
        item {
            DescriptionSection(
                modifier = Modifier.padding(16.dp),
                tagline = movie.tagline,
                value = movie.overview,
                fallbackValue = trending?.overview
            )
        }
        item {
            CharacterSection(
                modifier = Modifier.fillParentMaxWidth(),
                characters = movie.credits?.cast
            )
        }
        if (trailer != null) {
            item {
                Text(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "Trailer",
                    style = Platform.typography().headlineSmall
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp)
                        .aspectRatio(16F/9F)
                        .clip(MaterialTheme.shapes.medium),
                ) {
                    VideoPlayer(
                        modifier = Modifier.fillMaxSize(),
                        url = "https://youtube.com/watch?v=${trailer.key}",
                        autoPlay = false
                    )
                }
            }
        }
    }
}