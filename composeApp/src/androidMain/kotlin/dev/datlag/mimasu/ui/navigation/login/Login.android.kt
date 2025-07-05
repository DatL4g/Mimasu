package dev.datlag.mimasu.ui.navigation.login

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import dev.datlag.mimasu.R

@Composable
actual fun rememberAppImage(): Painter {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.animated_launcher)

    return rememberAnimatedVectorPainter(image, atEnd = false)
}