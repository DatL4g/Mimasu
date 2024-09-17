package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

class VideoScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : VideoComponent, ComponentContext by componentContext {

    @Composable
    override fun renderCommon() {
        onRender {
            VideoScreen(this)
        }
    }

}