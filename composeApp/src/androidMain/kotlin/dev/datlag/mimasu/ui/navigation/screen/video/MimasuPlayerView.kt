package dev.datlag.mimasu.ui.navigation.screen.video

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import dev.datlag.mimasu.common.findActivity
import dev.datlag.mimasu.other.AudioHelper
import dev.datlag.mimasu.other.BrightnessHelper
import io.github.aakira.napier.Napier

@OptIn(UnstableApi::class)
class MimasuPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr), PlayerGestureOptions {

    private val playerGestureController = PlayerGestureController(context, this)
    private val brightnessHelper = (context.findActivity() ?: this.context?.findActivity())?.let(::BrightnessHelper)
    private val audioHelper = AudioHelper(context)

    private var audioProgress: Int = audioHelper.volume ?: 0

    init {
        setOnTouchListener(playerGestureController)

        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    private fun toggleController() {
        if (isControllerFullyVisible) {
            hideController()
        } else {
            showController()
        }
    }

    override fun onSingleTap() {
        toggleController()
    }

    override fun onDoubleTapCenterScreen() {
        if (player?.isPlaying == true) {
            player?.pause()
        } else {
            player?.play()
        }
    }

    override fun onDoubleTapLeftScreen() {
        player?.seekBack()
    }

    override fun onDoubleTapRightScreen() {
        player?.seekForward()
    }

    override fun onSwipeLeftScreen(distanceY: Float) {
        // update brightness
    }

    override fun onSwipeRightScreen(distanceY: Float) {
        // update volume
    }

    override fun onZoom() {
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    }

    override fun onMinimize() {
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
    }

    override fun getViewMeasures(): Pair<Int, Int> {
        return width to height
    }
}