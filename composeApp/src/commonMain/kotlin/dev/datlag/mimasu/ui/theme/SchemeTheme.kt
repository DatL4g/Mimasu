package dev.datlag.mimasu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import com.kmpalette.DominantColorState
import com.kmpalette.rememberPainterDominantColorState
import com.materialkolor.DynamicMaterialTheme
import com.mayakapps.kache.InMemoryKache
import com.mayakapps.kache.KacheStrategy
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.platform.CombinedPlatformMaterialTheme
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.scopeCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic

data object SchemeTheme {

    private val movieCache = InMemoryKache<Int, ColorState>(
        maxSize = 5 * 1024 * 1024
    ) {
        strategy = KacheStrategy.LRU
    }

    @JvmStatic
    internal fun movieColor(key: Int): ColorState? = scopeCatching {
        movieCache.getIfAvailable(key)
    }.getOrNull()

    @JvmStatic
    internal suspend fun movieColor(key: Int, color: suspend () -> ColorState) = suspendCatching {
        movieCache.put(key) { color() }
    }.isSuccess

    @JvmStatic
    internal suspend fun movieColor(key: Int, color: ColorState) = movieColor(key) { color }

    @Composable
    fun createMovie(
        key: Int,
        defaultColor: Color? = Platform.colorScheme().primary,
        defaultOnColor: Color? = defaultColor?.let {
            val tint = Platform.colorScheme().contentColorFor(it)

            if (tint.isSpecified) {
                tint
            } else {
                null
            }
        } ?: defaultColor?.plainOn
    ): Updater<Painter> {
        val primaryColor = defaultColor ?: Platform.colorScheme().primary

        val state = rememberPainterDominantColorState(
            defaultColor = movieColor(key)?.primary?.let(::Color) ?: primaryColor,
            defaultOnColor = movieColor(key)?.onPrimary?.let(::Color) ?: defaultOnColor ?: primaryColor.plainOn
        )

        val colorState = remember(state.color, state.onColor) {
            ColorState(state.color.toArgb(), state.onColor.toArgb())
        }

        LaunchedEffect(colorState) {
            movieColor(key, colorState)
        }

        return Updater(
            state = state,
            scope = rememberCoroutineScope()
        )
    }

    @Serializable
    data class ColorState(
        val primary: Int,
        val onPrimary: Int = Color(primary).plainOn.toArgb()
    ) {
        constructor(state: DominantColorState<*>) : this(
            primary = state.color.toArgb(),
            onPrimary = state.onColor.toArgb()
        )

        constructor(updater: Updater<*>) : this(updater.state)
    }

    data class Updater<T : Any>(
        internal val state: DominantColorState<T>,
        val scope: CoroutineScope
    ) {
        val color: Color
            get() = state.color

        val onColor: Color
            get() = state.onColor

        fun updateFrom(image: T) = scope.launch {
            suspendCatching {
                state.updateFrom(image)
            }.isSuccess
        }
    }
}

val Color.plainOn
    get() = if (this.luminance() > 0.5F) {
        Color.Black
    } else {
        Color.White
    }

@Composable
fun MovieTheme(
    key: Int,
    defaultColor: Color? = Platform.colorScheme().primary,
    defaultOnColor: Color? = defaultColor?.let {
        val tint = Platform.colorScheme().contentColorFor(it)

        if (tint.isSpecified) {
            tint
        } else {
            null
        }
    } ?: defaultColor?.plainOn,
    content: @Composable (SchemeTheme.Updater<Painter>) -> Unit
) {
    val state = SchemeTheme.createMovie(
        key = key,
        defaultColor = SchemeTheme.movieColor(key)?.primary?.let(::Color) ?: defaultColor,
        defaultOnColor = SchemeTheme.movieColor(key)?.onPrimary?.let(::Color) ?: defaultOnColor
    )

    DynamicMaterialTheme(
        primary = state.color,
        animate = true
    ) {
        CombinedPlatformMaterialTheme(
            colorScheme = MaterialTheme.colorScheme
        ) {
            content(state)
        }
    }
}