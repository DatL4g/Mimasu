package dev.datlag.mimasu.tv.common

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onPlaced
import androidx.tv.material3.MaterialTheme

@Composable
fun Modifier.onFirstGainingVisibility(onGainingVisibility: () -> Unit): Modifier {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isVisible) { if (isVisible) onGainingVisibility() }

    return onPlaced { isVisible = true }
}

@Composable
fun Modifier.requestFocusOnFirstGainingVisibility(): Modifier {
    val focusRequester = remember { FocusRequester() }
    return focusRequester(focusRequester).onFirstGainingVisibility {
        focusRequester.requestFocus()
    }
}

@Composable
fun Modifier.immersiveListGradient(): Modifier {
    val color = MaterialTheme.colorScheme.surface

    return then(
        // x axis
        background(
            brush = Brush.linearGradient(
                0.2F to color.copy(alpha = 1F),
                0.8F to color.copy(alpha = 0.2F),
                0.9F to color.copy(alpha = 0F),
                start = Offset(0.0f, 0.0f),
                end = Offset(Float.POSITIVE_INFINITY, 0.0f)
            )
        )
    ).then(
        // y axis
        background(
            brush = Brush.linearGradient(
                0.1F to color.copy(alpha = 1F),
                0.4F to color.copy(alpha = 0.1F),
                0.9F to color.copy(alpha = 0F),
                start = Offset(0f, Float.POSITIVE_INFINITY),
                end = Offset(0f, 0f)
            )
        )
    )
}

fun Modifier.ifElse(
    condition: Boolean,
    ifTrueModifier: Modifier,
    elseModifier: Modifier = Modifier
): Modifier = then(if (condition) ifTrueModifier else elseModifier)