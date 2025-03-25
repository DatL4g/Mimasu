package dev.datlag.mimasu.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.localContentColor
import dev.tclement.fonticons.ExperimentalFontIconsApi
import dev.tclement.fonticons.FontIcon
import dev.tclement.fonticons.IconFont
import dev.tclement.fonticons.rememberVariableIconFont
import mimasu.composeapp.generated.resources.MaterialSymbolsRounded
import mimasu.composeapp.generated.resources.Res

data object MaterialSymbols {

    const val HOME = "home"
    const val SEARCH = "search"
    const val PERSON_PIN_CIRCLE = "person_pin_circle"
    const val MOVIE = "movie"
    const val TV = "tv"
    const val THUMBS_UP_DOWN = "thumbs_up_down"

    @Composable
    operator fun invoke(
        name: String,
        contentDescription: String?,
        modifier: Modifier = Modifier,
        tint: Color = Platform.localContentColor(),
        filled: Boolean = false
    ) {
        FontIcon(
            iconName = name,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint,
            iconFont = rememberFont(
                fill = if (filled) {
                    1F
                } else {
                    0F
                }
            )
        )
    }

    @OptIn(ExperimentalFontIconsApi::class)
    @Composable
    fun rememberFont(
        grade: Int = 24,
        fill: Float = 0f,
        manualOpsz: Boolean = false,
        opsz: Float = 24f
    ): IconFont = rememberVariableIconFont(
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
}