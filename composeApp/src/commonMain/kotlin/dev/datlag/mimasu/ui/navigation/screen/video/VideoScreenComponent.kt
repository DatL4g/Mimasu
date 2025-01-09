package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI
import org.kodein.di.instance

class VideoScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val shownInDialog: Boolean = false
) : VideoComponent, ComponentContext by componentContext {

    override val controller: VideoController by instance()

    @Composable
    override fun renderCommon() {
        onRender {
            VideoScreen(this)
        }
    }

}