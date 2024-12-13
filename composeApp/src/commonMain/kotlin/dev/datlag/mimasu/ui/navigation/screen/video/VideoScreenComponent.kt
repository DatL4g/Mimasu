package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.kodein.di.DI

class VideoScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    override val shownInDialog: Boolean = false
) : VideoComponent, ComponentContext by componentContext {

    private val _aspectRatio = MutableStateFlow(16F/9F)
    override val aspectRatio: StateFlow<Float> = _aspectRatio.asStateFlow()

    override val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override var togglePlayListener: ((Boolean) -> Unit)? = null

    @Composable
    override fun renderCommon() {
        onRender {
            VideoScreen(this)
        }
    }

    override fun updateAspectRatio(value: Float) {
        _aspectRatio.update { value }
    }

    override fun togglePlayPause(showControls: Boolean) {
        togglePlayListener?.invoke(showControls)
    }

}