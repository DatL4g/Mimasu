package dev.datlag.mimasu.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data object Colors {

    private const val THEME_LIGHT_PRIMARY = 0xff775084
    private const val THEME_LIGHT_ON_PRIMARY = 0xffffffff
    private const val THEME_LIGHT_PRIMARY_CONTAINER = 0xfff9d8ff
    private const val THEME_LIGHT_ON_PRIMARY_CONTAINER = 0xff2e0a3c

    private const val THEME_DARK_PRIMARY = 0xffe5b6f2
    private const val THEME_DARK_ON_PRIMARY = 0xff452152
    private const val THEME_DARK_PRIMARY_CONTAINER = 0xff5d386b
    private const val THEME_DARK_ON_PRIMARY_CONTAINER = 0xfff9d8ff

    private const val THEME_LIGHT_SECONDARY = 0xff69596d
    private const val THEME_LIGHT_ON_SECONDARY = 0xffffffff
    private const val THEME_LIGHT_SECONDARY_CONTAINER = 0xfff1dcf3
    private const val THEME_LIGHT_ON_SECONDARY_CONTAINER = 0xff241728

    private const val THEME_DARK_SECONDARY = 0xffd5c0d7
    private const val THEME_DARK_ON_SECONDARY = 0xff392c3d
    private const val THEME_DARK_SECONDARY_CONTAINER = 0xff514254
    private const val THEME_DARK_ON_SECONDARY_CONTAINER = 0xfff1dcf3

    private const val THEME_LIGHT_TERTIARY = 0xff81524f
    private const val THEME_LIGHT_ON_TERTIARY = 0xffffffff
    private const val THEME_LIGHT_TERTIARY_CONTAINER = 0xffffdad7
    private const val THEME_LIGHT_ON_TERTIARY_CONTAINER = 0xff331110

    private const val THEME_DARK_TERTIARY = 0xfff5b7b4
    private const val THEME_DARK_ON_TERTIARY = 0xff4c2524
    private const val THEME_DARK_TERTIARY_CONTAINER = 0xff663b39
    private const val THEME_DARK_ON_TERTIARY_CONTAINER = 0xffffdad7

    private const val THEME_LIGHT_ERROR = 0xffba1a1a
    private const val THEME_LIGHT_ON_ERROR = 0xffffffff
    private const val THEME_LIGHT_ERROR_CONTAINER = 0xffffdad6
    private const val THEME_LIGHT_ON_ERROR_CONTAINER = 0xff410002

    private const val THEME_DARK_ERROR = 0xffffb4ab
    private const val THEME_DARK_ON_ERROR = 0xff690005
    private const val THEME_DARK_ERROR_CONTAINER = 0xff93000a
    private const val THEME_DARK_ON_ERROR_CONTAINER = 0xffffdad6

    private const val THEME_LIGHT_BACKGROUND = 0xfffff7fb
    private const val THEME_LIGHT_ON_BACKGROUND = 0xff1f1a1f

    private const val THEME_DARK_BACKGROUND = 0xff161217
    private const val THEME_DARK_ON_BACKGROUND = 0xffeae0e7

    private const val THEME_LIGHT_SURFACE = 0xfffff7fb
    private const val THEME_LIGHT_ON_SURFACE = 0xff1f1a1f
    private const val THEME_LIGHT_SURFACE_VARIANT = 0xffebdfe9
    private const val THEME_LIGHT_ON_SURFACE_VARIANT = 0xff4c444d

    private const val THEME_DARK_SURFACE = 0xff161217
    private const val THEME_DARK_ON_SURFACE = 0xffeae0e7
    private const val THEME_DARK_SURFACE_VARIANT = 0xff4c444d
    private const val THEME_DARK_ON_SURFACE_VARIANT = 0xffcec3cd

    private const val THEME_LIGHT_OUTLINE = 0xff7d747d
    private const val THEME_LIGHT_INVERSE_SURFACE = 0xff342f34
    private const val THEME_LIGHT_INVERSE_ON_SURFACE = 0xfff8eef5
    private const val THEME_LIGHT_INVERSE_PRIMARY = 0xffe5b6f2

    private const val THEME_DARK_OUTLINE = 0xff988e97
    private const val THEME_DARK_INVERSE_SURFACE = 0xffeae0e7
    private const val THEME_DARK_INVERSE_ON_SURFACE = 0xff342f34
    private const val THEME_DARK_INVERSE_PRIMARY = 0xff775084

    fun getDarkScheme() = darkColorScheme(
        primary = Color(THEME_DARK_PRIMARY),
        onPrimary = Color(THEME_DARK_ON_PRIMARY),
        primaryContainer = Color(THEME_DARK_PRIMARY_CONTAINER),
        onPrimaryContainer = Color(THEME_DARK_ON_PRIMARY_CONTAINER),

        secondary = Color(THEME_DARK_SECONDARY),
        onSecondary = Color(THEME_DARK_ON_SECONDARY),
        secondaryContainer = Color(THEME_DARK_SECONDARY_CONTAINER),
        onSecondaryContainer = Color(THEME_DARK_ON_SECONDARY_CONTAINER),

        tertiary = Color(THEME_DARK_TERTIARY),
        onTertiary = Color(THEME_DARK_ON_TERTIARY),
        tertiaryContainer = Color(THEME_DARK_TERTIARY_CONTAINER),
        onTertiaryContainer = Color(THEME_DARK_ON_TERTIARY_CONTAINER),

        error = Color(THEME_DARK_ERROR),
        errorContainer = Color(THEME_DARK_ERROR_CONTAINER),
        onError = Color(THEME_DARK_ON_ERROR),
        onErrorContainer = Color(THEME_DARK_ON_ERROR_CONTAINER),

        background = Color(THEME_DARK_BACKGROUND),
        onBackground = Color(THEME_DARK_ON_BACKGROUND),

        surface = Color(THEME_DARK_SURFACE),
        onSurface = Color(THEME_DARK_ON_SURFACE),
        surfaceVariant = Color(THEME_DARK_SURFACE_VARIANT),
        onSurfaceVariant = Color(THEME_DARK_ON_SURFACE_VARIANT),

        outline = Color(THEME_DARK_OUTLINE),
        inverseSurface = Color(THEME_DARK_INVERSE_SURFACE),
        inverseOnSurface = Color(THEME_DARK_INVERSE_ON_SURFACE),
        inversePrimary = Color(THEME_DARK_INVERSE_PRIMARY)
    )

    fun getLightScheme() = lightColorScheme(
        primary = Color(THEME_LIGHT_PRIMARY),
        onPrimary = Color(THEME_LIGHT_ON_PRIMARY),
        primaryContainer = Color(THEME_LIGHT_PRIMARY_CONTAINER),
        onPrimaryContainer = Color(THEME_LIGHT_ON_PRIMARY_CONTAINER),

        secondary = Color(THEME_LIGHT_SECONDARY),
        onSecondary = Color(THEME_LIGHT_ON_SECONDARY),
        secondaryContainer = Color(THEME_LIGHT_SECONDARY_CONTAINER),
        onSecondaryContainer = Color(THEME_LIGHT_ON_SECONDARY_CONTAINER),

        tertiary = Color(THEME_LIGHT_TERTIARY),
        onTertiary = Color(THEME_LIGHT_ON_TERTIARY),
        tertiaryContainer = Color(THEME_LIGHT_TERTIARY_CONTAINER),
        onTertiaryContainer = Color(THEME_LIGHT_ON_TERTIARY_CONTAINER),

        error = Color(THEME_LIGHT_ERROR),
        errorContainer = Color(THEME_LIGHT_ERROR_CONTAINER),
        onError = Color(THEME_LIGHT_ON_ERROR),
        onErrorContainer = Color(THEME_LIGHT_ON_ERROR_CONTAINER),

        background = Color(THEME_LIGHT_BACKGROUND),
        onBackground = Color(THEME_LIGHT_ON_BACKGROUND),

        surface = Color(THEME_LIGHT_SURFACE),
        onSurface = Color(THEME_LIGHT_ON_SURFACE),
        surfaceVariant = Color(THEME_LIGHT_SURFACE_VARIANT),
        onSurfaceVariant = Color(THEME_LIGHT_ON_SURFACE_VARIANT),

        outline = Color(THEME_LIGHT_OUTLINE),
        inverseSurface = Color(THEME_LIGHT_INVERSE_SURFACE),
        inverseOnSurface = Color(THEME_LIGHT_INVERSE_ON_SURFACE),
        inversePrimary = Color(THEME_LIGHT_INVERSE_PRIMARY)
    )
}

@Composable
expect fun Colors.dynamicDark(): ColorScheme

@Composable
expect fun Colors.dynamicLight(): ColorScheme