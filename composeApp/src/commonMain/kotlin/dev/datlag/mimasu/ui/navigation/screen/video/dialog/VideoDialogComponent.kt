package dev.datlag.mimasu.ui.navigation.screen.video.dialog

import dev.datlag.mimasu.ui.navigation.DialogComponent
import dev.datlag.mimasu.ui.navigation.screen.video.VideoComponent
import dev.datlag.mimasu.ui.navigation.screen.video.VideoController

interface VideoDialogComponent : DialogComponent {
    val videoComponent: VideoComponent
    val videoController: VideoController
}