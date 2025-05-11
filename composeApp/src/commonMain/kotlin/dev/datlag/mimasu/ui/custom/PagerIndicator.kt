package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import kotlin.math.roundToInt

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPageFraction: State<Float>,
    modifier: Modifier = Modifier,
    activeDotColor: Color = Platform.colorScheme().onBackground,
    dotColor: Color = activeDotColor.copy(alpha = 0.3F),
    dotPainter: Painter = CirclePainter,
    dotCount: Int = 5,
    normalDotSize: Dp = 6.dp,
    activeDotSize: Dp = 8.dp,
    minDotSize: Dp = 4.dp,
    space: Dp = 8.dp,
    orientation: Orientation = Orientation.Horizontal,
    onAfterDraw: DrawScope.() -> Unit = { }
) {
    val adjustedDotCount = remember(pageCount, dotCount) {
        when {
            dotCount >= pageCount -> pageCount
            else -> if (dotCount % 2 == 0) dotCount - 1 else dotCount
        }
    }
    val density = LocalDensity.current
    val dotMinSizePx = with(density) { minDotSize.toPx() }
    val dotNormalSizePx = with(density) { normalDotSize.toPx() }
    val dotSizePx = with(density) { activeDotSize.toPx() }
    val spacePx = with(density) { space.toPx() }
    val mainAxisSize = activeDotSize * adjustedDotCount + space * (adjustedDotCount - 1)

    fun calculateTargetDotStateForPage(i: Int, page: Int): DotState {
        return calculateTargetDotSizeForPage(
            i = i,
            currentPage = page,
            pageCount = pageCount,
            dotCount = adjustedDotCount
        )
    }

    fun DotState.size(): Float {
        return when (this) {
            is DotState.Selected -> dotSizePx
            is DotState.Normal -> dotNormalSizePx
            is DotState.SmallEdge -> dotMinSizePx
            is DotState.Invisible -> 0F
        }
    }

    fun DotState.color(): Color {
        return when (this) {
            is DotState.Selected -> activeDotColor
            else -> dotColor
        }
    }

    Canvas(
        modifier = modifier
            .width(if (orientation == Orientation.Horizontal) mainAxisSize else activeDotSize)
            .height(if (orientation == Orientation.Horizontal) activeDotSize else mainAxisSize)
    ) {
        val pagerFraction = currentPageFraction.value
        val itemsCount = (pagerFraction - adjustedDotCount / 2).coerceIn(
            minimumValue = 0F,
            maximumValue = pageCount - adjustedDotCount.toFloat()
        )
        val scroll = -itemsCount * (dotSizePx + spacePx)
        val (firstVisible, lastVisible) = calculateVisibleDotIndices(
            dotCount = adjustedDotCount,
            currentPage = pagerFraction.roundToInt(),
            pageCount = pageCount
        ).let { (first, second) ->
            (first - 1).coerceAtLeast(0) to (second + 1).coerceAtMost(pageCount - 1)
        }

        translate(
            left = if (orientation == Orientation.Horizontal) scroll else 0F,
            top = if (orientation == Orientation.Vertical) scroll else 0F
        ) {
            for (i in firstVisible..lastVisible) {
                val dotStart = i * (dotSizePx + spacePx)
                val pagerFractionInt = pagerFraction.toInt()
                val scrollFraction = pagerFraction - pagerFractionInt
                val currentDotState = calculateTargetDotStateForPage(i, page = pagerFractionInt)
                val futureDotState = calculateTargetDotStateForPage(i, page = pagerFractionInt + 1)
                val targetDotSize = lerp(currentDotState.size(), futureDotState.size(), scrollFraction)
                val targetDotColor = androidx.compose.ui.graphics.lerp(currentDotState.color(), futureDotState.color(), scrollFraction)
                val dotColorFilter = ColorFilter.tint(targetDotColor)
                val mainAxisStart = dotStart + dotSizePx / 2F - targetDotSize / 2F
                val crossAxisStart = dotSizePx / 2F - targetDotSize / 2F
                val left = if (orientation == Orientation.Horizontal) mainAxisStart else crossAxisStart
                val top = if (orientation == Orientation.Horizontal) crossAxisStart else mainAxisStart

                with(dotPainter) {
                    translate(
                        left = left,
                        top = top
                    ) {
                        if (intrinsicSize == Size.Unspecified) {
                            draw(
                                size = Size(targetDotSize, targetDotSize),
                                colorFilter = dotColorFilter
                            )
                        } else {
                            scale(
                                scaleX = targetDotSize / intrinsicSize.width,
                                scaleY = targetDotSize / intrinsicSize.height,
                                pivot = Offset(0F, 0F)
                            ) {
                                draw(
                                    size = intrinsicSize,
                                    colorFilter = dotColorFilter
                                )
                            }
                        }
                    }
                }

                onAfterDraw()
            }
        }
    }
}

private fun calculateVisibleDotIndices(
    dotCount: Int,
    currentPage: Int,
    pageCount: Int,
): Pair<Int, Int> {

    val firstVisible: Int
    val lastVisible: Int

    if (currentPage < pageCount / 2) {
        firstVisible = (currentPage - dotCount / 2).coerceAtLeast(0)
        lastVisible = (firstVisible + (dotCount - 1))
    } else {
        lastVisible = (currentPage + dotCount / 2).coerceAtMost(pageCount - 1)
        firstVisible = (lastVisible - (dotCount - 1))
    }

    return firstVisible to lastVisible
}

private fun calculateTargetDotSizeForPage(
    i: Int,
    currentPage: Int,
    pageCount: Int,
    dotCount: Int,
): DotState {
    val onTheSidesDotCount = dotCount / 2
    val (firstVisible, lastVisible) = calculateVisibleDotIndices(
        dotCount = dotCount,
        currentPage = currentPage,
        pageCount = pageCount,
    )

    return when {
        currentPage == i -> DotState.Selected
        currentPage - i in -(onTheSidesDotCount - 1)..<onTheSidesDotCount -> DotState.Normal
        currentPage + onTheSidesDotCount >= pageCount && i > currentPage - (dotCount - (pageCount - currentPage - 1) - 1) -> DotState.Normal
        currentPage - onTheSidesDotCount <= 0 && i < currentPage + (dotCount - currentPage - 1) -> DotState.Normal
        currentPage + onTheSidesDotCount == i && i == pageCount - 1 -> DotState.Normal
        dotCount == pageCount -> DotState.Normal
        i in firstVisible..lastVisible -> DotState.SmallEdge
        else -> DotState.Invisible
    }
}

private sealed interface DotState {
    data object Normal : DotState
    data object Selected : DotState
    data object SmallEdge : DotState
    data object Invisible : DotState
}

private object CirclePainter : Painter() {
    override val intrinsicSize: Size = Size.Unspecified

    override fun DrawScope.onDraw() {
        drawCircle(Color.Black)
    }
}