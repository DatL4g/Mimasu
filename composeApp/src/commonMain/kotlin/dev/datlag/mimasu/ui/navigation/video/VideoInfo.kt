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
    forceCompact: Boolean = false,
    videoContent: @Composable (VideoInfo) -> Unit
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    if (watchType != null && (forceCompact || windowSize.isCompactWidth())) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            stickyHeader {
                videoContent(
                    VideoInfo(
                        modifier = Modifier.fillParentMaxWidth(),
                        showingCompact = true
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
                (watchType.episodeInfo.overview ?: watchType.seasonInfo.overview ?: watchType.showInfo.overview)?.let { overview ->
                    item {
                        Text(
                            modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                            text = overview
                        )
                    }
                }
            }
        }
    } else {
        videoContent(
            VideoInfo(
                modifier = Modifier.fillMaxSize(),
                showingCompact = false
            )
        )
    }
}

data class VideoInfo(
    val modifier: Modifier,
    val showingCompact: Boolean
)