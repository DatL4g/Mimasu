package dev.datlag.mimasu.ui.navigation.screen.video.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.swipeable
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import dev.datlag.mimasu.common.isFullyExpandedOrTargeted
import dev.datlag.mimasu.common.times
import dev.datlag.mimasu.other.ContentDetails
import dev.datlag.mimasu.ui.custom.WindowSize
import dev.datlag.mimasu.ui.custom.calculateWindowWidthSize
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.ifFalse
import dev.datlag.tooling.compose.ifTrue
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun VideoDialogScreen(
    component: VideoDialogComponent,
    padding: PaddingValues = PaddingValues(16.dp)
) {
    val isCompactScreen = calculateWindowWidthSize() is WindowSize.Compact

    val collapsedHeight = remember(padding) { 72.dp + padding.calculateBottomPadding() }
    val density = LocalDensity.current
    val collapsedHeightPx = remember(collapsedHeight, density) { with(density) { collapsedHeight.toPx() } }

    val contentPadding by ContentDetails.padding.collectAsStateWithLifecycle()
    val contentBottomPadding = remember(contentPadding) { contentPadding.calculateBottomPadding() }
    var isDragging by remember { mutableStateOf(false) }

    var offsetY by remember(isCompactScreen) { mutableStateOf(0F) }
    var fullHeightPx by remember { mutableStateOf(0f) }
    val availableHeightPx = remember(fullHeightPx, contentPadding, density) {
        max(fullHeightPx - with(density) { contentBottomPadding.toPx() }, 0F)
    }

    LaunchedEffect(contentBottomPadding) {
        if (!isDragging) {
            val nearestAnchor = listOf(0F, availableHeightPx - collapsedHeightPx).minByOrNull { abs(it - offsetY) } ?: 0F
            offsetY = nearestAnchor
        }
    }

    val progress by remember(availableHeightPx, offsetY, collapsedHeightPx) { derivedStateOf {
        if (availableHeightPx > 0F) {
            1F - (offsetY / (availableHeightPx - collapsedHeightPx))
        } else {
            1F
        }
    } }

    val collapsed by remember(progress) {
        derivedStateOf {
            progress <= 0.01F
        }
    }

    LaunchedEffect(collapsed) {
        component.videoController.controlsAvailable = !collapsed
    }

    val draggableState = rememberDraggableState { delta ->
        offsetY = (offsetY + delta).coerceIn(0F, max(availableHeightPx - collapsedHeightPx, 0F))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                fullHeightPx = it.height.toFloat()
            }
    ) {
        val direction = LocalLayoutDirection.current
        val topPadding by remember(progress) {
            derivedStateOf {
                lerp(padding.calculateTopPadding(), 0.dp, progress)
            }
        }
        val bottomPadding by remember(progress) {
            derivedStateOf {
                lerp(padding.calculateBottomPadding(), 0.dp, progress)
            }
        }
        val startPadding by remember(progress) {
            derivedStateOf {
                lerp(padding.calculateStartPadding(direction), 0.dp, progress)
            }
        }
        val endPadding by remember(progress) {
            derivedStateOf {
                lerp(padding.calculateEndPadding(direction), 0.dp, progress)
            }
        }

        val defaultBackground = Platform.colorScheme().background
        val cardBackground = CardDefaults.elevatedCardColors().containerColor
        val background by remember(progress) {
            derivedStateOf {
                lerp(cardBackground, defaultBackground, progress)
            }
        }

        val defaultColor = Platform.colorScheme().onBackground
        val cardColor = CardDefaults.elevatedCardColors().contentColor
        val color by remember(progress) {
            derivedStateOf {
                lerp(cardColor, defaultColor, progress)
            }
        }

        val targetShape = Platform.shapes().small
        val shape by remember(progress) {
            derivedStateOf {
                val reversedProgress = abs(1F - progress)

                targetShape.copy(
                    topStart = targetShape.topStart.times(reversedProgress, density),
                    topEnd = targetShape.topEnd.times(reversedProgress, density),
                    bottomStart = targetShape.bottomStart.times(reversedProgress, density),
                    bottomEnd = targetShape.bottomEnd.times(reversedProgress, density),
                )
            }
        }
        val defaultRipple = LocalRippleConfiguration.current

        CompositionLocalProvider(
            LocalRippleConfiguration provides RippleConfiguration(
                color = Color.Transparent,
                rippleAlpha = RippleAlpha(0F, 0F, 0F, 0F)
            )
        ) {
            ElevatedCard(
                modifier = Modifier
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = collapsedHeight)
                    .fillMaxHeight(progress)
                    .padding(PaddingValues(
                        start = startPadding,
                        top = topPadding,
                        end = endPadding,
                        bottom = bottomPadding
                    )),
                shape = shape,
                enabled = progress <= 0.01F,
                onClick = {
                    offsetY = 0F
                },
                colors = CardDefaults.elevatedCardColors(
                    containerColor = background,
                    contentColor = color
                )
            ) {
                CompositionLocalProvider(
                    LocalRippleConfiguration provides defaultRipple
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .draggable(
                                state = draggableState,
                                orientation = Orientation.Vertical,
                                enabled = isCompactScreen && !Platform.rememberIsTv(),
                                onDragStarted = {
                                    component.videoController.controlsAvailable = false
                                    isDragging = true
                                },
                                onDragStopped = {
                                    val nearestAnchor = listOf(0F, availableHeightPx - collapsedHeightPx).minByOrNull { abs(it - offsetY) } ?: 0F
                                    offsetY = nearestAnchor

                                    component.videoController.controlsAvailable = !collapsed
                                    isDragging = false
                                }
                            )
                    ) {
                        val baseModifier = if (isCompactScreen && !Platform.rememberIsTv()) {
                            Modifier
                                .fillMaxWidth(max(lerp(0.33F, progress * 2F, progress), 0.33F))
                                .aspectRatio(16F/9F)
                        } else {
                            Modifier.fillMaxSize()
                        }

                        Box(
                            modifier = baseModifier.background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            component.videoComponent.render()
                        }
                        AnimatedVisibility(
                            modifier = Modifier.weight(1F).fillMaxHeight(),
                            visible = progress <= 0.05F && availableHeightPx > 0F,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Hello World",
                                    softWrap = true,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2,
                                    color = Platform.colorScheme().onBackground,
                                    style = Platform.typography().titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        AnimatedVisibility(
                            modifier = Modifier.fillMaxHeight(),
                            visible = collapsed && availableHeightPx > 0F,
                            enter = fadeIn() + slideInHorizontally { it / 2 },
                            exit = fadeOut() + slideOutHorizontally { it / 2 }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        component.videoController.togglePlayPause(false)
                                    }
                                ) {
                                    val playing by component.videoController.isPlaying.collectAsStateWithLifecycle()

                                    Icon(
                                        imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        component.dismiss()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}