package dev.datlag.mimasu.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun Colors.getDarkScheme() = darkColorScheme(
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

fun Colors.getLightScheme() = lightColorScheme(
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

@Composable
expect fun Colors.dynamicDark(): ColorScheme

@Composable
expect fun Colors.dynamicLight(): ColorScheme