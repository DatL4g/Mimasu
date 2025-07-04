package dev.datlag.mimasu.tv.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.contentColorFor
import com.eygraber.compose.placeholder.PlaceholderDefaults

@Composable
fun PlaceholderDefaults.fadeHighlightColor(
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    alpha: Float = 0.3F
): Color = backgroundColor.copy(alpha = alpha)

@Composable
fun PlaceholderDefaults.color(
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    contentAlpha: Float = 0.1F
): Color = contentColor.copy(alpha = contentAlpha).compositeOver(backgroundColor)