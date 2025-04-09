package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Face2
import androidx.compose.material.icons.rounded.Face4
import androidx.compose.material.icons.rounded.Face5
import androidx.compose.material.icons.rounded.Face6
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Work
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
import dev.datlag.mimasu.tmdb.model.details.Movie
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
    const val TUNE = "tune"
    const val CLOSE = "close"
    const val ARROW_BACK_IOS_NEW = "arrow_back_ios_new"
    const val EVENT = "event"
    const val SCHEDULE = "schedule"
    const val REQUEST_QUOTE = "request_quote"
    const val PAYMENTS = "payments"
    const val RSS_FEED = "rss_feed"
    const val KEYBOARD_ARROW_DOWN = "keyboard_arrow_down"
    const val KEYBOARD_ARROW_UP = "keyboard_arrow_up"
    const val PLAY_ARROW = "play_arrow"
    const val GLOBE_LOCATION_PIN = "globe_location_pin"
    const val APARTMENT = "apartment"
    const val BOOKMARK = "bookmark"
    const val BOOKMARK_ADD = "bookmark_add"
    const val WORK = "work"
    const val PERSON = "person"
    const val CAKE = "cake"
    const val DECEASED = "deceased"

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

    @Composable
    operator fun invoke(
        cast: Movie.Credits.Cast?,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        tint: Color = Platform.localContentColor(),
        filled: Boolean = false,
        fallback: ImageVector? = fallbackFromName(cast?.let { nameFor(it) } ?: FACE_5)
    ) = invoke(
        name = cast?.let { nameFor(it) } ?: FACE_5,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
        filled = filled,
        fallback = fallback
    )

    @Composable
    operator fun invoke(
        crew: Movie.Credits.Crew?,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        tint: Color = Platform.localContentColor(),
        filled: Boolean = false,
        fallback: ImageVector? = fallbackFromName(crew?.let { nameFor(it) } ?: FACE_5)
    ) = invoke(
        name = crew?.let { nameFor(it) } ?: FACE_5,
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

        name.equals(TUNE, ignoreCase = true) -> Icons.Rounded.Tune
        name.equals(CLOSE, ignoreCase = true) -> Icons.Rounded.Close
        name.equals(ARROW_BACK_IOS_NEW, ignoreCase = true) -> Icons.Rounded.ArrowBackIosNew
        name.equals(EVENT, ignoreCase = true) -> Icons.Rounded.Event
        name.equals(SCHEDULE, ignoreCase = true) -> Icons.Rounded.Schedule
        name.equals(REQUEST_QUOTE, ignoreCase = true) -> Icons.Rounded.RequestQuote
        name.equals(PAYMENTS, ignoreCase = true) -> Icons.Rounded.Payments
        name.equals(RSS_FEED, ignoreCase = true) -> Icons.Rounded.RssFeed
        name.equals(KEYBOARD_ARROW_DOWN, ignoreCase = true) -> Icons.Rounded.KeyboardArrowDown
        name.equals(KEYBOARD_ARROW_UP, ignoreCase = true) -> Icons.Rounded.KeyboardArrowUp
        name.equals(PLAY_ARROW, ignoreCase = true) -> Icons.Rounded.PlayArrow
        name.equals(GLOBE_LOCATION_PIN, ignoreCase = true) -> Icons.Rounded.Public
        name.equals(APARTMENT, ignoreCase = true) -> Icons.Rounded.Apartment
        name.equals(BOOKMARK, ignoreCase = true) -> Icons.Rounded.Bookmark
        name.equals(BOOKMARK_ADD, ignoreCase = true) -> Icons.Rounded.BookmarkAdd
        name.equals(WORK, ignoreCase = true) -> Icons.Rounded.Work
        name.equals(PERSON, ignoreCase = true) -> Icons.Rounded.Person
        name.equals(CAKE, ignoreCase = true) -> Icons.Rounded.Cake
        name.equals(DECEASED, ignoreCase = true) -> Icons.Rounded.LocalFlorist
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

    private fun nameFor(cast: Movie.Credits.Cast): String = when {
        cast.isFemale -> FACE_4
        cast.isMale -> FACE_6
        cast.isNonBinary -> FACE_2
        else -> FACE_5
    }

    private fun nameFor(crew: Movie.Credits.Crew): String = when {
        crew.isFemale -> FACE_4
        crew.isMale -> FACE_6
        crew.isNonBinary -> FACE_2
        else -> FACE_5
    }
}