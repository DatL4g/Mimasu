package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import kotlin.math.roundToInt

@Composable
fun PagerWormIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeDotColor: Color = Platform.colorScheme().onBackground,
    dotColor: Color = activeDotColor.copy(alpha = 0.3F),
    activeDotSize: Dp = 8.dp,
    dotSize: Dp = 4.dp,
    space: Dp = 8.dp,
    count: Int = 5,
    orientation: Orientation = Orientation.Horizontal
) {
    PagerWormIndicator(
        pageCount = pagerState.pageCount,
        currentPageFraction = remember(pagerState) {
            derivedStateOf {
                pagerState.currentPage + pagerState.currentPageOffsetFraction
            }
        },
        modifier = modifier,
        activeDotColor = activeDotColor,
        dotColor = dotColor,
        activeDotSize = activeDotSize,
        dotSize = dotSize,
        space = space,
        count = count,
        orientation = orientation
    )
}

@Composable
fun PagerWormIndicator(
    pageCount: Int,
    currentPageFraction: State<Float>,
    modifier: Modifier = Modifier,
    activeDotColor: Color = Platform.colorScheme().onBackground,
    dotColor: Color = activeDotColor.copy(alpha = 0.3F),
    activeDotSize: Dp = 8.dp,
    dotSize: Dp = 4.dp,
    space: Dp = 8.dp,
    count: Int = 5,
    orientation: Orientation = Orientation.Horizontal
) {
    val wormLinePos = wormLinePosAsState(currentPageFraction)
    val density = LocalDensity.current
    val dotSizePx = with(density) { activeDotSize.toPx() }
    val spacePx = with(density) { space.toPx() }

    PagerIndicator(
        pageCount = pageCount,
        currentPageFraction = currentPageFraction,
        modifier = modifier,
        activeDotColor = dotColor,
        dotColor = dotColor,
        dotCount = count,
        normalDotSize = activeDotSize,
        activeDotSize = activeDotSize,
        minDotSize = dotSize,
        space = space,
        orientation = orientation,
        onAfterDraw = {
            val lineStart = wormLinePos.value.first * (dotSizePx + spacePx)
            val lineEnd = wormLinePos.value.second * (dotSizePx + spacePx)
            val lineStartX = when (orientation) {
                Orientation.Horizontal -> lineStart + dotSizePx / 2F
                Orientation.Vertical -> dotSizePx / 2F
            }
            val lineStartY = when (orientation) {
                Orientation.Horizontal -> dotSizePx / 2F
                Orientation.Vertical -> lineStart + dotSizePx / 2F
            }
            val lineEndX = when (orientation) {
                Orientation.Horizontal -> lineEnd + dotSizePx / 2F
                Orientation.Vertical -> dotSizePx / 2F
            }
            val lineEndY = when (orientation) {
                Orientation.Horizontal -> dotSizePx / 2F
                Orientation.Vertical -> lineEnd + dotSizePx / 2F
            }

            drawLine(
                color = activeDotColor,
                start = Offset(lineStartX, lineStartY),
                end = Offset(lineEndX, lineEndY),
                strokeWidth = dotSizePx,
                cap = StrokeCap.Round
            )
        }
    )
}

@Composable
private fun wormLinePosAsState(
    currentPageFraction: State<Float>
): State<Pair<Float, Float>> {
    return remember {
        var lastAnchor = currentPageFraction.value.toInt()

        derivedStateOf {
            val cur = currentPageFraction.value

            val pagerFractionInt = cur.toInt()
            val scrollFraction = cur - pagerFractionInt

            if (cur > lastAnchor) {
                if (cur >= lastAnchor + 1) {
                    lastAnchor = cur.roundToInt()
                }

                val start = lastAnchor + (2F * scrollFraction - 1F).coerceIn(0F, 1F)
                val end = lastAnchor + (scrollFraction * 2F).coerceIn(0F, 1F)

                start to end
            } else if (cur < lastAnchor) {
                if (cur < lastAnchor - 1) {
                    lastAnchor = cur.roundToInt()
                }

                val start = lastAnchor - (1F - 2F * scrollFraction).coerceIn(0F, 1F)
                val end = lastAnchor - (2F - 2F * scrollFraction).coerceIn(0F, 1F)

                start to end
            } else {
                lastAnchor.toFloat() to lastAnchor.toFloat()
            }
        }
    }
}