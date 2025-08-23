package dev.datlag.mimasu.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePainter.State
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.model.HasKana
import dev.datlag.tooling.country.Country
import dev.datlag.tooling.country.Japan
import dev.datlag.tooling.scopeCatching
import io.tolgee.Tolgee
import io.tolgee.stringResource
import org.jetbrains.compose.resources.StringResource
import org.kodein.di.DI
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

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
    error: Painter? = null,
    onLoading: ((State.Loading) -> Unit)? = null,
    onSuccess: ((State.Success) -> Unit)? = null,
    onError: ((State.Error) -> Unit)? = null,
): AsyncImagePainter {
    val data = remember(models) { models.filterNotNull() }

    if (data.isEmpty()) {
        return rememberAsyncImagePainter(
            model = null,
            error = error,
            contentScale = contentScale,
            onError = {
                onError?.invoke(it)
            }
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

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@Composable
fun Modifier.handleDPadKeyEvents(
    onLeft: (() -> Boolean)? = null,
    onRight: (() -> Boolean)? = null,
    onUp: (() -> Boolean)? = null,
    onDown: (() -> Boolean)? = null,
    onEnter: (() -> Boolean)? = null
): Modifier = onKeyEvent { event ->
    if (event.type == KeyEventType.KeyUp) {
        when (event.key) {
            Key.DirectionLeft, Key.SystemNavigationLeft -> {
                onLeft?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.DirectionRight, Key.SystemNavigationRight -> {
                onRight?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.DirectionUp, Key.SystemNavigationUp -> {
                onUp?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.DirectionDown, Key.SystemNavigationDown -> {
                onDown?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> {
                onEnter?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
        }
    }
    return@onKeyEvent false
}

@Composable
fun Modifier.handlePlayerKeyEvents(
    play: (() -> Boolean)? = null,
    playPause: (() -> Boolean)? = null,
    pause: (() -> Boolean)? = null,
    rewind: (() -> Boolean)? = null,
    forward: (() -> Boolean)? = null
): Modifier = onKeyEvent { event ->
    if (event.type == KeyEventType.KeyUp) {
        when (event.key) {
            Key.MediaPlay -> {
                play?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.MediaPlayPause, Key.K, Key.Spacebar -> {
                playPause?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.MediaPause -> {
                pause?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.MediaRewind, Key.J -> {
                rewind?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
            Key.MediaFastForward, Key.L -> {
                forward?.invoke().also { consume -> return@onKeyEvent consume ?: false }
            }
        }
    }
    return@onKeyEvent false
}

fun <T : Any> NavController.bringToFront(route: T, builder: NavOptionsBuilder.() -> Unit) {
    val isRouteOnBackStack = scopeCatching {
        getBackStackEntry(route)
    }.isSuccess

    if (isRouteOnBackStack) {
        popBackStack(route = route, inclusive = false)
    } else {
        navigate(route, builder)
    }
}

@Composable
private fun tolgeeInstance(
    di: DI = localDI()
): Tolgee? = with(di) {
    val uiInstance by instanceOrNull<Tolgee>("UiTolgee")

    return@with uiInstance ?: run {
        val instance by instanceOrNull<Tolgee>()

        instance ?: Tolgee.instanceOrNull
    }
}

@Composable
fun uiStringRes(resource: StringResource): String {
    return tolgeeInstance()?.let {
        stringResource(tolgee = it, resource = resource)
    } ?: stringResource(resource = resource)
}

@Composable
fun uiStringRes(resource: StringResource, vararg formatArgs: Any): String {
    return tolgeeInstance()?.let {
        stringResource(tolgee = it, resource = resource, formatArgs = formatArgs)
    } ?: stringResource(resource = resource, formatArgs = formatArgs)
}

@Composable
fun rememberLocaleIsJapanese(): Boolean {
    val locale = Locale.current

    return remember(locale) {
        locale.language.equals("ja", ignoreCase = true)
                || locale.toLanguageTag().equals("ja", ignoreCase = true)
                || run {
            val country = Country.forCodeOrNull(locale.language)
                ?: Country.forCodeOrNull(locale.toLanguageTag())

            country is Japan
        }
    }
}

@Composable
fun rememberResolvedKanaTitles(hasKana: HasKana?): Pair<String?, String?> {
    val japaneseAllowed = rememberLocaleIsJapanese()

    if (hasKana == null) {
        return null to null
    }
    val title = remember(hasKana) {
        hasKana.kanaSource?.trim()?.ifBlank { null }
    }
    val originalTitle = remember(hasKana) {
        hasKana.kanaBackupSource?.trim()?.ifBlank { null }
    }

    return remember(title, originalTitle, japaneseAllowed, hasKana) {
        if (japaneseAllowed || title.isNullOrBlank()) {
            title to originalTitle
        } else {
            if (hasKana.kanaSourceIsJapanese) {
                if (originalTitle.isNullOrBlank() || hasKana.kanaBackupSourceIsJapanese) {
                    val normalTitle = (hasKana.kanaSourceRomaji ?: title)
                    val otherTitle = (originalTitle ?: title)

                    normalTitle to if (normalTitle.equals(otherTitle, ignoreCase = true)) {
                        null
                    } else {
                        otherTitle
                    }
                } else {
                    title to originalTitle
                }
            } else {
                title to originalTitle
            }
        }
    }
}