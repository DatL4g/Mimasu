package dev.datlag.mimasu.ui.custom

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

data object MaterialSymbols {

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

    public val Rating18Up: ImageVector
        get() {
            return _Rating18Up ?: ImageVector.Builder(
                name = "Icon18_up_rating",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 960f,
                viewportHeight = 960f
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(340f, 420f)
                    verticalLineToRelative(150f)
                    quadToRelative(0f, 13f, 8.5f, 21.5f)
                    reflectiveQuadTo(370f, 600f)
                    reflectiveQuadToRelative(21.5f, -8.5f)
                    reflectiveQuadTo(400f, 570f)
                    verticalLineToRelative(-180f)
                    quadToRelative(0f, -13f, -8.5f, -21.5f)
                    reflectiveQuadTo(370f, 360f)
                    horizontalLineToRelative(-60f)
                    quadToRelative(-13f, 0f, -21.5f, 8.5f)
                    reflectiveQuadTo(280f, 390f)
                    reflectiveQuadToRelative(8.5f, 21.5f)
                    reflectiveQuadTo(310f, 420f)
                    close()
                    moveToRelative(160f, 180f)
                    horizontalLineToRelative(100f)
                    quadToRelative(17f, 0f, 28.5f, -11.5f)
                    reflectiveQuadTo(640f, 560f)
                    verticalLineToRelative(-160f)
                    quadToRelative(0f, -17f, -11.5f, -28.5f)
                    reflectiveQuadTo(600f, 360f)
                    horizontalLineTo(500f)
                    quadToRelative(-17f, 0f, -28.5f, 11.5f)
                    reflectiveQuadTo(460f, 400f)
                    verticalLineToRelative(160f)
                    quadToRelative(0f, 17f, 11.5f, 28.5f)
                    reflectiveQuadTo(500f, 600f)
                    moveToRelative(20f, -40f)
                    verticalLineToRelative(-60f)
                    horizontalLineToRelative(60f)
                    verticalLineToRelative(60f)
                    close()
                    moveToRelative(0f, -100f)
                    verticalLineToRelative(-60f)
                    horizontalLineToRelative(60f)
                    verticalLineToRelative(60f)
                    close()
                    moveTo(200f, 840f)
                    quadToRelative(-33f, 0f, -56.5f, -23.5f)
                    reflectiveQuadTo(120f, 760f)
                    verticalLineToRelative(-560f)
                    quadToRelative(0f, -33f, 23.5f, -56.5f)
                    reflectiveQuadTo(200f, 120f)
                    horizontalLineToRelative(560f)
                    quadToRelative(33f, 0f, 56.5f, 23.5f)
                    reflectiveQuadTo(840f, 200f)
                    verticalLineToRelative(560f)
                    quadToRelative(0f, 33f, -23.5f, 56.5f)
                    reflectiveQuadTo(760f, 840f)
                    close()
                    moveToRelative(0f, -80f)
                    horizontalLineToRelative(560f)
                    verticalLineToRelative(-560f)
                    horizontalLineTo(200f)
                    close()
                    moveToRelative(0f, -560f)
                    verticalLineToRelative(560f)
                    close()
                }
            }.build().also {
                _Rating18Up = it
            }
        }

    private var _Rating18Up: ImageVector? = null

}