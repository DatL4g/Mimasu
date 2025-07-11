package dev.datlag.mimasu.common

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.FluentMaterials
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.mimasu.ui.LocalDarkMode
import dev.datlag.mimasu.ui.custom.MediumLargeIconButtonTokens
import dev.datlag.tooling.Platform
import kotlin.math.abs
import kotlin.math.absoluteValue

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun Modifier.hazeEffect(
    state: HazeState,
    style: HazeStyle = when {
        Platform.isApple -> CupertinoMaterials.thin()
        Platform.isWindows -> FluentMaterials.acrylicBase(isDark = LocalDarkMode.current)
        else -> HazeMaterials.thin()
    },
    listState: LazyListState,
    progressive: Boolean = false
) = Modifier.hazeEffect(
    state = state,
    style = style
) {
    alpha = if (listState.firstVisibleItemIndex == 0) {
        if (progressive) {
            listState.layoutInfo.visibleItemsInfo.firstOrNull()?.let {
                (it.offset / it.size.toFloat()).absoluteValue
            } ?: if (listState.firstVisibleItemScrollOffset == 0) {
                0F
            } else {
                1F
            }
        } else {
            if (listState.firstVisibleItemScrollOffset == 0) {
                0F
            } else {
                1F
            }
        }
    } else {
        1F
    }
}

@Composable
fun LazyListState.isSticking(index: Int): Boolean {
    return remember(this) {
        derivedStateOf {
            val firstVisible = layoutInfo.visibleItemsInfo.firstOrNull()
            firstVisible?.index == index && firstVisible.offset == -layoutInfo.beforeContentPadding
        }
    }.value
}

suspend fun PointerInputScope.detectPinchGestures(
    pass: PointerEventPass = PointerEventPass.Main,
    onGestureStart: (PointerInputChange) -> Unit = { },
    onGesture: (centroid: Offset, zoom: Float) -> Unit,
    onGestureEnd: (PointerInputChange) -> Unit = { }
) {
    awaitEachGesture {
        var zoom = 1F
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        val down = awaitFirstDown(requireUnconsumed = false, pass = pass)

        onGestureStart(down)

        var pointer = down
        var pointerId = down.id

        do {
            val event = awaitPointerEvent(pass = pass)
            val canceled = event.changes.any { it.isConsumed }

            if (!canceled) {
                val pointerInputChange = event.changes.firstOrNull {
                    it.id == pointerId
                } ?: event.changes.first()

                pointerId = pointerInputChange.id
                pointer = pointerInputChange

                val zoomChange = event.calculateZoom()

                if (!pastTouchSlop) {
                    zoom *= zoomChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1F - zoom) * centroidSize

                    if (zoomMotion > touchSlop) {
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)

                    if (zoomChange != 1F) {
                        onGesture(
                            centroid,
                            zoomChange
                        )
                        event.changes.forEach { it.consume() }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })

        onGestureEnd(pointer)
    }
}

@Composable
fun FontFamily.toExpressiveTypography(): Typography {
    return remember(this) {
        Typography(
            displayLarge = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp,
                lineHeight = 72.0.sp,
                letterSpacing = (-0.5).sp
            ),
            displayMedium = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp,
                lineHeight = 58.0.sp,
                letterSpacing = (-0.2).sp
            ),
            displaySmall = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 48.0.sp,
                letterSpacing = 0.0.sp
            ),
            headlineLarge = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.SemiBold,
                fontSize = 36.sp,
                lineHeight = 44.0.sp,
                letterSpacing = 0.0.sp
            ),
            headlineMedium = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                lineHeight = 38.0.sp,
                letterSpacing = 0.0.sp
            ),
            headlineSmall = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                lineHeight = 34.0.sp,
                letterSpacing = 0.0.sp
            ),
            titleLarge = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Medium, // Often remains Medium or SemiBold
                fontSize = 22.sp,
                lineHeight = 28.0.sp,
                letterSpacing = 0.0.sp
            ),
            titleMedium = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.0.sp,
                letterSpacing = 0.2.sp
            ),
            titleSmall = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.0.sp,
                letterSpacing = 0.1.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.0.sp,
                letterSpacing = 0.5.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.0.sp,
                letterSpacing = 0.2.sp
            ),
            bodySmall = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.0.sp,
                letterSpacing = 0.4.sp,
            ),
            labelLarge = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.0.sp,
                letterSpacing = 0.1.sp
            ),
            labelMedium = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.0.sp,
                letterSpacing = 0.5.sp
            ),
            labelSmall = TextStyle(
                fontFamily = this,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.0.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}

fun IconButtonDefaults.mediumLargeContainerSize(
    widthOption: IconButtonDefaults.IconButtonWidthOption = IconButtonDefaults.IconButtonWidthOption.Uniform
): DpSize {
    val horizontalSpace = when (widthOption) {
        IconButtonDefaults.IconButtonWidthOption.Narrow -> MediumLargeIconButtonTokens.NarrowLeadingSpace + MediumLargeIconButtonTokens.NarrowTrailingSpace
        IconButtonDefaults.IconButtonWidthOption.Uniform -> MediumLargeIconButtonTokens.UniformLeadingSpace + MediumLargeIconButtonTokens.UniformTrailingSpace
        IconButtonDefaults.IconButtonWidthOption.Wide -> MediumLargeIconButtonTokens.WideLeadingSpace + MediumLargeIconButtonTokens.WideTrailingSpace
        else -> 0.dp
    }

    return DpSize(
        width = MediumLargeIconButtonTokens.IconSize + horizontalSpace,
        height = MediumLargeIconButtonTokens.ContainerHeight
    )
}

fun WindowSizeClass.isCompactWidth(): Boolean {
    return when {
        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> false // expanded
        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> false // medium
        else -> true
    }
}

fun WindowSizeClass.isMediumWidth(): Boolean {
    return when {
        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> false // expanded
        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> true // medium
        else -> false
    }
}

fun WindowSizeClass.isExpandedWidth(): Boolean {
    return when {
        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> true // expanded
        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> false // medium
        else -> false
    }
}

fun WindowSizeClass.isCompactHeight(): Boolean {
    return when {
        this.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND) -> false // expanded
        this.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) -> false // medium
        else -> true
    }
}

fun WindowSizeClass.isMediumHeight(): Boolean {
    return when {
        this.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND) -> false // expanded
        this.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) -> true // medium
        else -> false
    }
}

fun WindowSizeClass.isExpandedHeight(): Boolean {
    return when {
        this.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND) -> true // expanded
        this.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) -> false // medium
        else -> false
    }
}