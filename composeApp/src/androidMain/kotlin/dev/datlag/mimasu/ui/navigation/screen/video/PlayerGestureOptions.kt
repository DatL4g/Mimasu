package dev.datlag.mimasu.ui.navigation.screen.video

interface PlayerGestureOptions {

    fun onSingleTap()

    fun onDoubleTapCenterScreen()

    fun onDoubleTapLeftScreen()

    fun onDoubleTapRightScreen()

    fun onSwipeLeftScreen(distanceY: Float)

    fun onSwipeRightScreen(distanceY: Float)

    fun onZoom()

    fun onMinimize()

    /**
     *  Returns a pair of the width and height of the view this listener is used for
     *  These measures change when the screen orientation changes or fullscreen is entered, thus
     *  needs to be refreshed manually all the time when needed.
     */
    fun getViewMeasures(): Pair<Int, Int>
}