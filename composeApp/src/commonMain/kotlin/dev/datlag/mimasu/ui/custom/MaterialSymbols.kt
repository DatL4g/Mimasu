package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face2
import androidx.compose.material.icons.rounded.Face4
import androidx.compose.material.icons.rounded.Face5
import androidx.compose.material.icons.rounded.Face6
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import dev.datlag.mimasu.composeapp.generated.resources.MaterialSymbolsRounded
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformIcon
import dev.datlag.tooling.compose.platform.localContentColor
import dev.datlag.tooling.compose.withIOContext
import dev.tclement.fonticons.ExperimentalFontIconsApi
import dev.tclement.fonticons.FontIcon
import dev.tclement.fonticons.IconFont
import dev.tclement.fonticons.VariableIconFont
import dev.tclement.fonticons.createVariableIconFont
import kotlinx.atomicfu.atomic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.getSystemResourceEnvironment

data object MaterialSymbols {

    const val HOME = "home"
    const val SEARCH = "search"
    const val PERSON_PIN_CIRCLE = "person_pin_circle"
    const val MOVIE = "movie"
    const val TV = "tv"
    const val THUMBS_UP_DOWN = "thumbs_up_down"
    const val FACE_2 = "face_2" // non binary
    const val FACE_4 = "face_4" // woman
    const val FACE_5 = "face_5" // not specified
    const val FACE_6 = "face_6" // man

    private const val DEFAULT_GRADE = 24
    private const val DEFAULT_OPSZ = 24F

    private val defaultNonFilledFont = atomic<IconFont?>(null)
    private val defaultFilledFont = atomic<IconFont?>(null)

    @Composable
    operator fun invoke(
        name: String,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        tint: Color = Platform.localContentColor(),
        filled: Boolean = false,
        fallback: ImageVector? = fallbackFromName(name)
    ) {
        val font = rememberAsyncFont(
            fill = if (filled) {
                1F
            } else {
                0F
            }
        )

        if (font == null) {
            if (fallback != null) {
                PlatformIcon(
                    imageVector = fallback,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    tint = tint
                )
            } else {
                Spacer(modifier = modifier)
            }
        } else {
            FontIcon(
                iconName = name,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint,
                iconFont = font
            )
        }
    }

    @Composable
    operator fun invoke(
        person: People?,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        tint: Color = Platform.localContentColor(),
        filled: Boolean = false,
        fallback: ImageVector? = fallbackFromName(person?.let { nameFor(it) } ?: FACE_5)
    ) = invoke(
        name = person?.let { nameFor(it) } ?: FACE_5,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        filled = filled,
        fallback = fallback
    )

    private fun fallbackFromName(name: String): ImageVector? = when {
        name.equals(HOME, ignoreCase = true) -> Icons.Rounded.Home
        name.equals(SEARCH, ignoreCase = true) -> Icons.Rounded.Search
        name.equals(PERSON_PIN_CIRCLE, ignoreCase = true) -> Icons.Rounded.PersonPinCircle
        name.equals(MOVIE, ignoreCase = true) -> Icons.Rounded.Movie
        name.equals(TV, ignoreCase = true) -> Icons.Rounded.Tv

        name.equals(FACE_2, ignoreCase = true) -> Icons.Rounded.Face2
        name.equals(FACE_4, ignoreCase = true) -> Icons.Rounded.Face4
        name.equals(FACE_5, ignoreCase = true) -> Icons.Rounded.Face5
        name.equals(FACE_6, ignoreCase = true) -> Icons.Rounded.Face6
        else -> null
    }

    @OptIn(ExperimentalResourceApi::class, ExperimentalFontIconsApi::class)
    @Composable
    fun asyncVariableFont(
        fontResource: FontResource,
        weights: Array<FontWeight>,
        fontVariationSettings: FontVariation.Settings = FontVariation.Settings(),
        fontFeatureSettings: String? = null
    ): VariableIconFont? {
        val density = LocalDensity.current

        return produceState<VariableIconFont?>(initialValue = null, key1 = fontResource) {
            value = withIOContext {
                createVariableIconFont(
                    fontResource = fontResource,
                    weights = weights,
                    fontVariationSettings = fontVariationSettings,
                    fontFeatureSettings = fontFeatureSettings,
                    resourceEnvironment = getSystemResourceEnvironment(),
                    density = density
                )
            }
        }.value
    }

    @Composable
    fun rememberAsyncFont(
        grade: Int = DEFAULT_GRADE,
        fill: Float = 0f,
        manualOpsz: Boolean = false,
        opsz: Float = DEFAULT_OPSZ
    ): IconFont? {

        @Composable
        fun create() = asyncVariableFont(
            fontResource = Res.font.MaterialSymbolsRounded,
            weights = arrayOf(
                FontWeight.W100,
                FontWeight.W200,
                FontWeight.W300,
                FontWeight.W400,
                FontWeight.W500,
                FontWeight.W600,
                FontWeight.W700,
                FontWeight.W800,
                FontWeight.W900,
            ),
            fontVariationSettings = FontVariation.Settings(*buildList {
                add(FontVariation.grade(grade))
                add(FontVariation.Setting("FILL", fill))
                if (manualOpsz) {
                    add(FontVariation.Setting("opsz", opsz))
                }
            }.toTypedArray())
        )

        if (grade == DEFAULT_GRADE && opsz == DEFAULT_OPSZ) {
            when {
                fill <= 0F -> return defaultNonFilledFont.value ?: create().also {
                    defaultNonFilledFont.compareAndSet(null, it)
                }
                fill >= 1F -> return defaultFilledFont.value ?: create().also {
                    defaultFilledFont.compareAndSet(null, it)
                }
            }
        }

        return create()
    }

    private fun nameFor(person: People): String = when {
        person.isFemale -> FACE_4
        person.isMale -> FACE_6
        person.isNonBinary -> FACE_2
        else -> FACE_5
    }
}