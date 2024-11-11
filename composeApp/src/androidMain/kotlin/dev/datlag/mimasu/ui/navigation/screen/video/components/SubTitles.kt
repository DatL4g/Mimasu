package dev.datlag.mimasu.ui.navigation.screen.video.components

import android.content.Context
import android.text.Layout
import android.util.TypedValue
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.core.util.TypedValueCompat
import androidx.media3.common.text.Cue
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle

@Composable
fun SubTitles(
    state: VideoPlayerState,
    modifier: Modifier = Modifier
) {
    val cues by state.cues.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = modifier,
        visible = cues.isNotEmpty(),
        enter = expandIn(expandFrom = Alignment.CenterStart),
        exit = shrinkOut(shrinkTowards = Alignment.CenterEnd)
    ) {
        Text(
            text = buildAnnotatedSubTitle(LocalContext.current, cues, LocalTextStyle.current),
            color = Color.White,
            textAlign = TextAlign.Center,
            softWrap = true
        )
    }
}

private fun buildAnnotatedSubTitle(
    context: Context,
    cues: Collection<Cue>,
    defaultStyle: TextStyle
): AnnotatedString {
    return buildAnnotatedString {
        cues.forEachIndexed { index, cue ->
            if (cue.text.isNullOrBlank()) {
                return@forEachIndexed
            }

            val textAlign = when (cue.textAlignment) {
                Layout.Alignment.ALIGN_CENTER -> TextAlign.Center
                Layout.Alignment.ALIGN_OPPOSITE -> TextAlign.End
                Layout.Alignment.ALIGN_NORMAL -> TextAlign.Start
                else -> defaultStyle.textAlign
            }

            val fontSize = when (cue.textSizeType) {
                Cue.TEXT_SIZE_TYPE_FRACTIONAL -> (cue.textSize * 16).sp
                Cue.TEXT_SIZE_TYPE_ABSOLUTE -> {
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PX, cue.textSize, context.resources.displayMetrics
                    ).sp
                }
                else -> defaultStyle.fontSize
            }

            withStyle(
                style = TextStyle(
                    fontSize = fontSize,
                    textAlign = textAlign
                ).toSpanStyle()
            ) {
                append(cue.text?.ifBlank { null })
            }

            if (index < cues.size - 1) {
                append("\n")
            }
        }
    }
}