package dev.datlag.mimasu.ui.navigation.screen.video

import dev.datlag.mimasu.ui.navigation.Component

interface VideoComponent : Component {
    val shownInDialog: Boolean
    val controller: VideoController
}