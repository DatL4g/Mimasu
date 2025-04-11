package dev.datlag.mimasu.rive.common

import app.rive.runtime.kotlin.core.Alignment
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Loop
import dev.datlag.mimasu.rive.RiveAlignment
import dev.datlag.mimasu.rive.RiveFit
import dev.datlag.mimasu.rive.RiveLoop

fun RiveAlignment.toAndroid(): Alignment {
    return when (this) {
        is RiveAlignment.Top.Left -> Alignment.TOP_LEFT
        is RiveAlignment.Top.Center -> Alignment.TOP_CENTER
        is RiveAlignment.Top.Right -> Alignment.TOP_RIGHT

        is RiveAlignment.Center.Left -> Alignment.CENTER_LEFT
        is RiveAlignment.Center.Right -> Alignment.CENTER_RIGHT
        is RiveAlignment.Center -> Alignment.CENTER

        is RiveAlignment.Bottom.Left -> Alignment.BOTTOM_LEFT
        is RiveAlignment.Bottom.Center -> Alignment.BOTTOM_CENTER
        is RiveAlignment.Bottom.Right -> Alignment.BOTTOM_RIGHT
    }
}

fun RiveFit.toAndroid(): Fit {
    return when (this) {
        is RiveFit.Fill -> Fit.FILL
        is RiveFit.Contain -> Fit.CONTAIN
        is RiveFit.Cover -> Fit.COVER
        is RiveFit.Fit.Width -> Fit.FIT_WIDTH
        is RiveFit.Fit.Height -> Fit.FIT_HEIGHT
        is RiveFit.None -> Fit.NONE
        is RiveFit.ScaleDown -> Fit.SCALE_DOWN
        is RiveFit.Layout -> Fit.LAYOUT
    }
}

fun RiveLoop.toAndroid(): Loop {
    return when (this) {
        is RiveLoop.OneShot -> Loop.ONESHOT
        is RiveLoop.Loop -> Loop.LOOP
        is RiveLoop.PingPong -> Loop.PINGPONG
        is RiveLoop.Auto -> Loop.AUTO
    }
}