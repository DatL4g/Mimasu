package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Face2
import androidx.compose.material.icons.rounded.Face4
import androidx.compose.material.icons.rounded.Face5
import androidx.compose.material.icons.rounded.Face6
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocalFlorist
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.RssFeed
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Work
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import dev.tclement.fonticons.LocalIconSize
import dev.tclement.fonticons.VariableIconFont
import dev.tclement.fonticons.createVariableIconFont
import dev.tclement.fonticons.painter.rememberFontIconPainter
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
    const val MAIL = "mail"
    const val PASSWORD = "password"
    const val VISIBILITY = "visibility"
    const val VISIBILITY_OFF = "visibility_off"
    const val CHECK_SMALL = "check_small"
    const val CLOSE_SMALL = "close_small"
    const val LOGOUT = "logout"
    const val ERROR = "error"
    const val HELP = "help"
    const val CODE = "code"
    const val CONTRACT = "contract"
    const val TODAY = "today"
    const val DATE_RANGE = "date_range"
    const val STEPPERS = "steppers"
    const val CHEVRON_RIGHT = "chevron_right"
    const val EXTENSION = "extension"
    const val CHECK = "check"
    const val HIDE_IMAGE = "hide_image"
    const val INFO = "info"
    const val OPEN_IN_BROWSER = "open_in_browser"
    const val ACCOUNT_CIRCLE = "account_circle"
    const val DOWNLOAD = "download"

    private const val DEFAULT_GRADE = 24
    private const val DEFAULT_OPSZ = 24F

    private val defaultNonFilledFont = atomic<IconFont?>(null)
    private val defaultFilledFont = atomic<IconFont?>(null)
    private var defaultNonFilledRecomposed by atomic<Boolean>(false)
    private var defaultFilledRecomposed by atomic<Boolean>(false)

    private var _GoogleGLogo: ImageVector? = null
    private var _Github: ImageVector? = null

    val GoogleGLogo: ImageVector
        get() {
            return _GoogleGLogo ?: ImageVector.Builder(
                name = "GoogleLogo",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF4285F4)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(22.56f, 12.25f)
                    curveToRelative(0f, -0.78f, -0.07f, -1.53f, -0.2f, -2.25f)
                    horizontalLineTo(12f)
                    verticalLineToRelative(4.26f)
                    horizontalLineToRelative(5.92f)
                    curveToRelative(-0.26f, 1.37f, -1.04f, 2.53f, -2.21f, 3.31f)
                    verticalLineToRelative(2.77f)
                    horizontalLineToRelative(3.57f)
                    curveToRelative(2.08f, -1.92f, 3.28f, -4.74f, 3.28f, -8.09f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF34A853)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12f, 23f)
                    curveToRelative(2.97f, 0f, 5.46f, -0.98f, 7.28f, -2.66f)
                    lineToRelative(-3.57f, -2.77f)
                    curveToRelative(-0.98f, 0.66f, -2.23f, 1.06f, -3.71f, 1.06f)
                    curveToRelative(-2.86f, 0f, -5.29f, -1.93f, -6.16f, -4.53f)
                    horizontalLineTo(2.18f)
                    verticalLineToRelative(2.84f)
                    curveTo(3.99f, 20.53f, 7.7f, 23f, 12f, 23f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFFBBC05)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(5.84f, 14.09f)
                    curveToRelative(-0.22f, -0.66f, -0.35f, -1.36f, -0.35f, -2.09f)
                    reflectiveCurveToRelative(0.13f, -1.43f, 0.35f, -2.09f)
                    verticalLineTo(7.07f)
                    horizontalLineTo(2.18f)
                    curveTo(1.43f, 8.55f, 1f, 10.22f, 1f, 12f)
                    reflectiveCurveToRelative(0.43f, 3.45f, 1.18f, 4.93f)
                    lineToRelative(2.85f, -2.22f)
                    lineToRelative(0.81f, -0.62f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFEA4335)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12f, 5.38f)
                    curveToRelative(1.62f, 0f, 3.06f, 0.56f, 4.21f, 1.64f)
                    lineToRelative(3.15f, -3.15f)
                    curveTo(17.45f, 2.09f, 14.97f, 1f, 12f, 1f)
                    curveTo(7.7f, 1f, 3.99f, 3.47f, 2.18f, 7.07f)
                    lineToRelative(3.66f, 2.84f)
                    curveToRelative(0.87f, -2.6f, 3.3f, -4.53f, 6.16f, -4.53f)
                    close()
                }
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(1f, 1f)
                    horizontalLineToRelative(22f)
                    verticalLineToRelative(22f)
                    horizontalLineTo(1f)
                    close()
                }
            }.build().also {
                _GoogleGLogo = it
            }
        }

    public val Github: ImageVector
        get() {
            return _Github ?: ImageVector.Builder(
                name = "Github",
                defaultWidth = 300.dp,
                defaultHeight = 300.dp,
                viewportWidth = 300f,
                viewportHeight = 300f
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.EvenOdd
                ) {
                    moveTo(150.001f, 0f)
                    curveTo(67.1687f, 0f, 0f, 68.8559f, 0f, 153.798f)
                    curveTo(0f, 221.749f, 42.9799f, 279.399f, 102.58f, 299.736f)
                    curveTo(110.077f, 301.159f, 112.829f, 296.399f, 112.829f, 292.337f)
                    curveTo(112.829f, 288.67f, 112.69f, 276.554f, 112.625f, 263.703f)
                    curveTo(70.8946f, 273.007f, 62.089f, 245.557f, 62.089f, 245.557f)
                    curveTo(55.2656f, 227.78f, 45.4341f, 223.053f, 45.4341f, 223.053f)
                    curveTo(31.8245f, 213.508f, 46.4599f, 213.704f, 46.4599f, 213.704f)
                    curveTo(61.5227f, 214.786f, 69.4539f, 229.555f, 69.4539f, 229.555f)
                    curveTo(82.8325f, 253.065f, 104.545f, 246.268f, 113.105f, 242.338f)
                    curveTo(114.451f, 232.398f, 118.338f, 225.61f, 122.628f, 221.772f)
                    curveTo(89.3107f, 217.883f, 54.2869f, 204.696f, 54.2869f, 145.765f)
                    curveTo(54.2869f, 128.974f, 60.1466f, 115.254f, 69.7421f, 104.483f)
                    curveTo(68.1846f, 100.607f, 63.0503f, 84.9671f, 71.1952f, 63.7826f)
                    curveTo(71.1952f, 63.7826f, 83.7914f, 59.6492f, 112.456f, 79.5475f)
                    curveTo(124.421f, 76.1398f, 137.254f, 74.4309f, 150.001f, 74.3723f)
                    curveTo(162.749f, 74.4309f, 175.591f, 76.1398f, 187.579f, 79.5475f)
                    curveTo(216.209f, 59.6492f, 228.787f, 63.7826f, 228.787f, 63.7826f)
                    curveTo(236.952f, 84.9671f, 231.815f, 100.607f, 230.258f, 104.483f)
                    curveTo(239.876f, 115.254f, 245.696f, 128.974f, 245.696f, 145.765f)
                    curveTo(245.696f, 204.836f, 210.605f, 217.843f, 177.203f, 221.65f)
                    curveTo(182.583f, 226.423f, 187.377f, 235.782f, 187.377f, 250.131f)
                    curveTo(187.377f, 270.71f, 187.203f, 287.271f, 187.203f, 292.337f)
                    curveTo(187.203f, 296.43f, 189.904f, 301.226f, 197.507f, 299.715f)
                    curveTo(257.075f, 279.356f, 300f, 221.726f, 300f, 153.798f)
                    curveTo(300f, 68.8559f, 232.841f, 0f, 150.001f, 0f)
                    close()
                }
            }.build().also {
                _Github = it
            }
        }

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

    @Composable
    fun rememberPainter(
        name: String,
        tint: Color = Platform.localContentColor(),
        filled: Boolean = false,
        fallback: ImageVector? = fallbackFromName(name),
        size: Dp = LocalIconSize.current
    ): Painter? {
        val font = rememberAsyncFont(
            fill = if (filled) {
                1F
            } else {
                0F
            }
        )

        return if (font == null) {
            if (fallback != null) {
                rememberVectorPainter(fallback)
            } else {
                null
            }
        } else {
            rememberFontIconPainter(
                iconName = name,
                tint = tint,
                iconFont = font,
                size = size
            )
        }
    }

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
        name.equals(MAIL, ignoreCase = true) -> Icons.Rounded.Mail
        name.equals(PASSWORD, ignoreCase = true) -> Icons.Rounded.Password
        name.equals(VISIBILITY, ignoreCase = true) -> Icons.Rounded.Visibility
        name.equals(VISIBILITY_OFF, ignoreCase = true) -> Icons.Rounded.VisibilityOff
        name.equals(CHECK_SMALL, ignoreCase = true) -> Icons.Rounded.Check
        name.equals(CLOSE_SMALL, ignoreCase = true) -> Icons.Rounded.Close
        name.equals(LOGOUT, ignoreCase = true) -> Icons.AutoMirrored.Rounded.Logout
        name.equals(ERROR, ignoreCase = true) -> Icons.Rounded.ErrorOutline
        name.equals(HELP, ignoreCase = true) -> Icons.AutoMirrored.Rounded.HelpOutline
        name.equals(CODE, ignoreCase = true) -> Icons.Rounded.Code
        name.equals(CONTRACT, ignoreCase = true) -> Icons.Rounded.Handshake
        name.equals(TODAY, ignoreCase = true) -> Icons.Rounded.Today
        name.equals(DATE_RANGE, ignoreCase = true) -> Icons.Rounded.DateRange
        name.equals(STEPPERS, ignoreCase = true) -> Icons.Rounded.MoreHoriz
        name.equals(CHEVRON_RIGHT, ignoreCase = true) -> Icons.Rounded.ChevronRight
        name.equals(EXTENSION, ignoreCase = true) -> Icons.Rounded.Extension
        name.equals(CHECK, ignoreCase = true) -> Icons.Rounded.Check
        name.equals(HIDE_IMAGE, ignoreCase = true) -> Icons.Outlined.HideImage
        name.equals(INFO, ignoreCase = true) -> Icons.Rounded.Info
        name.equals(OPEN_IN_BROWSER, ignoreCase = true) -> Icons.Rounded.OpenInBrowser
        name.equals(ACCOUNT_CIRCLE, ignoreCase = true) -> Icons.Rounded.AccountCircle
        name.equals(DOWNLOAD, ignoreCase = true) -> Icons.Rounded.Download
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