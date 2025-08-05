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
import androidx.compose.ui.text.font.GenericFontFamily
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_Bold
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_ExtraBold
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_ExtraLight
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_Light
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_Medium
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_Regular
import dev.datlag.mimasu.composeapp.generated.resources.Manrope_SemiBold
import dev.datlag.mimasu.composeapp.generated.resources.NotoSansJP_VariableFont_wght
import dev.datlag.mimasu.composeapp.generated.resources.NotoSansKR_VariableFont_wght
import dev.datlag.mimasu.composeapp.generated.resources.NotoSansSC_VariableFont_wght
import dev.datlag.mimasu.composeapp.generated.resources.Res
import kotlinx.atomicfu.atomic
import kotlinx.serialization.Serializable
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
            manropeFamily = loadFontFamily(Type.Manrope)
        }
        val fallbackFont = loadCJKFallback()

        return manropeFamily ?: fallbackFont ?: FontFamily.Default
    }

    @Composable
    fun loadCJKFallback(): FontFamily? {
        if (jpFamily == null) {
            jpFamily = loadFontFamily(Type.NotoJP)
        }
        if (krFamily == null) {
            krFamily = loadFontFamily(Type.NotoKR)
        }
        if (scFamily == null) {
            scFamily = loadFontFamily(Type.NotoSC)
        }

        return scFamily ?: jpFamily ?: krFamily
    }

    @Composable
    private fun loadFontFamily(type: Type): FontFamily? {
        val fontFamilyResolver = LocalFontFamilyResolver.current
        val fonts = loadVariableFont(type)
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
    private fun loadVariableFont(type: Type): List<Font> {
        val extraLight by preloadFont(type.extraLight, FontWeight.ExtraLight)
        val extraLightItalic by preloadFont(type.extraLight, FontWeight.ExtraLight, FontStyle.Italic)
        val light by preloadFont(type.light, FontWeight.Light)
        val lightItalic by preloadFont(type.light, FontWeight.Light, FontStyle.Italic)
        val regular by preloadFont(type.normal, FontWeight.Normal)
        val regularItalic by preloadFont(type.normal, FontWeight.Normal, FontStyle.Italic)
        val medium by preloadFont(type.medium, FontWeight.Medium)
        val mediumItalic by preloadFont(type.medium, FontWeight.Medium, FontStyle.Italic)
        val semiBold by preloadFont(type.semiBold, FontWeight.SemiBold)
        val semiBoldItalic by preloadFont(type.semiBold, FontWeight.SemiBold, FontStyle.Italic)
        val bold by preloadFont(type.bold, FontWeight.Bold)
        val boldItalic by preloadFont(type.bold, FontWeight.Bold, FontStyle.Italic)
        val extraBold by preloadFont(type.extraBold, FontWeight.ExtraBold)
        val extraBoldItalic by preloadFont(type.extraBold, FontWeight.ExtraBold, FontStyle.Italic)

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

    @Serializable
    sealed interface Type {

        val extraLight: FontResource
            get() = light

        val light: FontResource
            get() = normal

        val normal: FontResource

        val medium: FontResource
            get() = normal

        val semiBold: FontResource
            get() = medium

        val bold: FontResource
            get() = semiBold

        val extraBold: FontResource
            get() = bold

        @Serializable
        data object Manrope : Type {
            override val extraLight: FontResource = Res.font.Manrope_ExtraLight
            override val light: FontResource = Res.font.Manrope_Light
            override val normal: FontResource = Res.font.Manrope_Regular
            override val medium: FontResource = Res.font.Manrope_Medium
            override val semiBold: FontResource = Res.font.Manrope_SemiBold
            override val bold: FontResource = Res.font.Manrope_Bold
            override val extraBold: FontResource = Res.font.Manrope_ExtraBold
        }

        @Serializable
        data object NotoJP : Type {
            override val normal: FontResource = Res.font.NotoSansJP_VariableFont_wght
        }

        @Serializable
        data object NotoKR : Type {
            override val normal: FontResource = Res.font.NotoSansKR_VariableFont_wght
        }

        @Serializable
        data object NotoSC : Type {
            override val normal: FontResource = Res.font.NotoSansSC_VariableFont_wght
        }
    }
}