package dev.datlag.mimasu.common

import com.vanniktech.locale.Country
import com.vanniktech.locale.Language
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import dev.datlag.tooling.Platform

fun Locale.Companion.default(): Locale {
    return fromOrNull(Locales.currentLocaleString()) ?: run {
        val all = Locales.currentLocaleStrings()

        for (l in all) {
            return fromOrNull(l) ?: continue
        }

        return fromOrNull(Locales.currentLocaleString()) ?: Locale(
            language = Language.ENGLISH,
            country = Country.USA
        )
    }
}

fun Locale.localized(): String = when {
    Platform.isIOS || Platform.isMacOS || Platform.isTVOS || Platform.isWatchOS -> this.appleAppStoreLocale().toString()
    Platform.isAndroid -> this.googlePlayStoreLocale().toString()
    else -> this.language.code
}