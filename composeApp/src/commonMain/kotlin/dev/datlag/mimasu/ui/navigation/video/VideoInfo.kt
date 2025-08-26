package dev.datlag.mimasu.ui.navigation.video

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.isCompactWidth
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography

@Composable
fun VideoInfo(
    watchType: VideoViewModel.WatchType?,
    contentPadding: PaddingValues,
    requestedLayout: VideoLayout = VideoLayout.Unknown,
    videoContent: @Composable (VideoInfo) -> Unit
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    if (watchType != null) {
        when (requestedLayout) {
            is VideoLayout.Portrait -> PortraitLayout(
                watchType = watchType,
                contentPadding = contentPadding,
                videoContent = videoContent
            )
            is VideoLayout.Landscape -> videoContent(
                VideoInfo(
                    modifier = Modifier.fillMaxSize(),
                    layout = VideoLayout.Landscape
                )
            )
            else -> {
                if (windowSize.isCompactWidth()) {
                    PortraitLayout(
                        watchType = watchType,
                        contentPadding = contentPadding,
                        videoContent = videoContent
                    )
                } else {
                    videoContent(
                        VideoInfo(
                            modifier = Modifier.fillMaxSize(),
                            layout = VideoLayout.Landscape
                        )
                    )
                }
            }
        }
    } else {
        videoContent(
            VideoInfo(
                modifier = Modifier.fillMaxSize(),
                layout = VideoLayout.Unknown
            )
        )
    }
}

@Composable
private fun PortraitLayout(
    watchType: VideoViewModel.WatchType,
    contentPadding: PaddingValues,
    videoContent: @Composable (VideoInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        stickyHeader {
            videoContent(
                VideoInfo(
                    modifier = Modifier.fillParentMaxWidth(),
                    layout = VideoLayout.Portrait
                )
            )
        }
        if (watchType is VideoViewModel.WatchType.Show) {
            watchType.seasonInfo.name?.ifBlank { null }?.let { season ->
                item {
                    Text(
                        modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                        text = season,
                        style = Platform.typography().labelMedium,
                        color = Platform.colorScheme().primary
                    )
                }
            }
            item {
                Text(
                    modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                    text = watchType.showInfo.name,
                    style = Platform.typography().titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            (watchType.episodeInfo.overview?.ifBlank { null } ?: watchType.seasonInfo.overview?.ifBlank { null } ?: watchType.showInfo.overview?.ifBlank { null })?.let { overview ->
                item {
                    Text(
                        modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                        text = overview
                    )
                }
            }
        }
    }
}

data class VideoInfo(
    val modifier: Modifier,
    val layout: VideoLayout
)