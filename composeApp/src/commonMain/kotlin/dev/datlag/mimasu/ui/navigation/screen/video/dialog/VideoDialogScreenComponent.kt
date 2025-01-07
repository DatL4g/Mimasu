package dev.datlag.mimasu.ui.navigation.screen.video.dialog

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.mimasu.ui.navigation.Component
import dev.datlag.mimasu.ui.navigation.screen.video.VideoComponent
import dev.datlag.mimasu.ui.navigation.screen.video.VideoController
import dev.datlag.mimasu.ui.navigation.screen.video.VideoScreenComponent
import org.kodein.di.DI

class VideoDialogScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onDismiss: () -> Unit
) : VideoDialogComponent, ComponentContext by componentContext {

    override val videoComponent: VideoComponent = VideoScreenComponent(
        componentContext = componentContext,
        di = di,
        shownInDialog = true
    )

    override val videoController: VideoController = videoComponent.controller

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun renderCommon(scope: SharedTransitionScope) = with(scope) {
        onRender {
            VideoDialogScreen(this@VideoDialogScreenComponent)
        }
    }

    override fun dismiss() {
        onDismiss()
    }
}