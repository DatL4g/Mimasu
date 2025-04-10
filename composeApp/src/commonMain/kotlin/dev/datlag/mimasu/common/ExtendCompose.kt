package dev.datlag.mimasu.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePainter.State
import coil3.compose.rememberAsyncImagePainter
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.FluentMaterials
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.datlag.mimasu.LocalDarkMode
import dev.datlag.tooling.Platform
import kotlin.math.absoluteValue

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        start = this.calculateStartPadding(direction) + other.calculateStartPadding(direction),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(direction) + other.calculateEndPadding(direction),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}

@Composable
operator fun PaddingValues.plus(all: Dp): PaddingValues {
    val direction = LocalLayoutDirection.current
    val other = PaddingValues(all)

    return PaddingValues(
        start = this.calculateStartPadding(direction) + other.calculateStartPadding(direction),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(direction) + other.calculateEndPadding(direction),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}

@Composable
fun PaddingValues.merge(other: PaddingValues): PaddingValues {
    val direction = LocalLayoutDirection.current

    return PaddingValues(
        start = max(this.calculateStartPadding(direction), other.calculateStartPadding(direction)),
        top = max(this.calculateTopPadding(), other.calculateTopPadding()),
        end = max(this.calculateEndPadding(direction), other.calculateEndPadding(direction)),
        bottom = max(this.calculateBottomPadding(), other.calculateBottomPadding())
    )
}

@Composable
fun PaddingValues.merge(all: Dp): PaddingValues {
    val direction = LocalLayoutDirection.current
    val other = PaddingValues(all)

    return PaddingValues(
        start = max(this.calculateStartPadding(direction), other.calculateStartPadding(direction)),
        top = max(this.calculateTopPadding(), other.calculateTopPadding()),
        end = max(this.calculateEndPadding(direction), other.calculateEndPadding(direction)),
        bottom = max(this.calculateBottomPadding(), other.calculateBottomPadding())
    )
}

@Composable
fun rememberNestedImagePainter(
    models: Collection<Any?>,
    contentScale: ContentScale = ContentScale.Crop,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
): AsyncImagePainter {
    val data = remember(models) { models.filterNotNull() }

    if (data.isEmpty()) {
        return rememberAsyncImagePainter(
            model = null,
            contentScale = contentScale
        )
    }

    return rememberAsyncImagePainter(
        model = data.first(),
        contentScale = contentScale,
        error = rememberNestedImagePainter(data.drop(1), contentScale),
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = {
            if (data.size <= 1) {
                onError?.invoke(it)
            }
        }
    )
}

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