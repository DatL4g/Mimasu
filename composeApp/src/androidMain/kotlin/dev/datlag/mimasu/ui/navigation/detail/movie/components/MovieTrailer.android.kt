package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dev.datlag.mimasu.core.YouTubeUtils
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun MovieTrailer(url: String?, modifier: Modifier) {
    val videoId = remember(url) { url?.let(YouTubeUtils::videoIdAsItOrFromUrl) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var playbackPosition by rememberSaveable { mutableFloatStateOf(0F) }
    var playVideo by remember(videoId) { mutableStateOf(false) }

    if (!videoId.isNullOrBlank()) {
        Card(
            onClick = {
                playVideo = true
            },
            modifier = modifier
                .aspectRatio(16/9F)
                .clip(Platform.shapes().medium),
            enabled = !playVideo
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val thumbnails = remember(videoId) { YouTubeUtils.thumbnailsForId(videoId) }

                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = thumbnails.firstOrNull(),
                    error = rememberNestedImagePainter(
                        models = thumbnails.drop(1),
                        contentScale = ContentScale.Crop
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                if (!playVideo) {
                    IconButton(
                        onClick = { playVideo = true },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5F), CircleShape)
                            .padding(8.dp)
                    ) {
                        MaterialSymbols(
                            modifier = Modifier.size(ButtonDefaults.ExtraLargeIconSize),
                            name = MaterialSymbols.PLAY_ARROW,
                            contentDescription = null,
                            filled = true
                        )
                    }
                }

                if (playVideo) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            YouTubePlayerView(ctx).apply {
                                enableAutomaticInitialization = false
                                lifecycleOwner.lifecycle.addObserver(this)

                                this.initialize(object : AbstractYouTubePlayerListener() {
                                    override fun onReady(youTubePlayer: YouTubePlayer) {
                                        super.onReady(youTubePlayer)

                                        youTubePlayer.loadVideo(videoId, playbackPosition)
                                    }

                                    override fun onCurrentSecond(
                                        youTubePlayer: YouTubePlayer,
                                        second: Float
                                    ) {
                                        super.onCurrentSecond(youTubePlayer, second)

                                        playbackPosition = second
                                    }
                                }, IFramePlayerOptions.Builder(ctx).autoplay(0).build())
                            }
                        }
                    )
                }
            }
        }
    }
}