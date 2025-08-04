package dev.datlag.mimasu.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_VariableFont_wght
import dev.datlag.mimasu.composeapp.generated.resources.NotoSansJP_VariableFont_wght
import dev.datlag.mimasu.composeapp.generated.resources.NotoSansKR_VariableFont_wght
import dev.datlag.mimasu.composeapp.generated.resources.NotoSansSC_VariableFont_wght
import dev.datlag.mimasu.composeapp.generated.resources.Res
import kotlinx.atomicfu.atomic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.preloadFont

data object Font {

    private var manropeFamily by atomic<FontFamily?>(null)
    private var jpFamily by atomic<FontFamily?>(null)
    private var krFamily by atomic<FontFamily?>(null)
    private var scFamily by atomic<FontFamily?>(null)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun manrope(): FontFamily {
        if (manropeFamily == null) {
            manropeFamily = loadFontFamily(Res.font.Manrope_VariableFont_wght)
        }
        val fallbackFont = loadCJKFallback()

        return manropeFamily ?: fallbackFont ?: FontFamily.Default
    }

    @Composable
    fun loadCJKFallback(): FontFamily? {
        if (jpFamily == null) {
            jpFamily = loadFontFamily(Res.font.NotoSansJP_VariableFont_wght)
        }
        if (krFamily == null) {
            krFamily = loadFontFamily(Res.font.NotoSansKR_VariableFont_wght)
        }
        if (scFamily == null) {
            scFamily = loadFontFamily(Res.font.NotoSansSC_VariableFont_wght)
        }

        return scFamily ?: jpFamily ?: krFamily
    }

    @Composable
    private fun loadFontFamily(
        resource: FontResource
    ): FontFamily? {
        val fontFamilyResolver = LocalFontFamilyResolver.current
        val fonts = loadVariableFont(resource)
        return if (fonts.isEmpty()) {
            null
        } else {
            val family = remember(fonts) { FontFamily(fonts) }

            LaunchedEffect(family) {
                fontFamilyResolver.preload(family)
            }

            family
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun loadVariableFont(resource: FontResource): List<Font> {
        val extraLight by preloadFont(resource, FontWeight.ExtraLight)
        val extraLightItalic by preloadFont(resource, FontWeight.ExtraLight, FontStyle.Italic)
        val light by preloadFont(resource, FontWeight.Light)
        val lightItalic by preloadFont(resource, FontWeight.Light, FontStyle.Italic)
        val regular by preloadFont(resource, FontWeight.Normal)
        val regularItalic by preloadFont(resource, FontWeight.Normal, FontStyle.Italic)
        val medium by preloadFont(resource, FontWeight.Medium)
        val mediumItalic by preloadFont(resource, FontWeight.Medium, FontStyle.Italic)
        val semiBold by preloadFont(resource, FontWeight.SemiBold)
        val semiBoldItalic by preloadFont(resource, FontWeight.SemiBold, FontStyle.Italic)
        val bold by preloadFont(resource, FontWeight.Bold)
        val boldItalic by preloadFont(resource, FontWeight.Bold, FontStyle.Italic)
        val extraBold by preloadFont(resource, FontWeight.ExtraBold)
        val extraBoldItalic by preloadFont(resource, FontWeight.ExtraBold, FontStyle.Italic)

        return remember(
            extraLight, extraLightItalic,
            light, lightItalic,
            regular, regularItalic,
            medium, mediumItalic,
            semiBold, semiBoldItalic,
            bold, boldItalic,
            extraBold, extraBoldItalic
        ) {
            listOfNotNull(
                extraLight, extraLightItalic,
                light, lightItalic,
                regular, regularItalic,
                medium, mediumItalic,
                semiBold, semiBoldItalic,
                bold, boldItalic,
                extraBold, extraBoldItalic
            )
        }
    }
}