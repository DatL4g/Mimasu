package dev.datlag.mimasu.ui.navigation.screen.video.dialog

import dev.datlag.mimasu.ui.navigation.DialogComponent
import dev.datlag.mimasu.ui.navigation.screen.video.VideoComponent

interface VideoDialogComponent : DialogComponent {
    val videoComponent: VideoComponent
}