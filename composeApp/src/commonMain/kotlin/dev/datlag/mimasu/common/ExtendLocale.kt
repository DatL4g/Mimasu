package dev.datlag.mimasu.common

import com.vanniktech.locale.Country
import com.vanniktech.locale.Language
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.tooling.Platform

fun Locale.Companion.default(): Locale {
    return fromOrNull(Locales.currentLocaleString()) ?: run {
        val all = Locales.currentLocaleStrings()

        for (l in all) {
            return fromOrNull(l) ?: continue
        }

        return fromOrNull(Locales.currentLocaleString()) ?: Locale(
            language = Language.ENGLISH,
            territory = Country.USA
        )
    }
}

fun Locale.localized(): String = when {
    Platform.isIOS || Platform.isMacOS || Platform.isTVOS || Platform.isWatchOS -> this.appleAppStoreLocale().toString()
    Platform.isAndroid -> this.googlePlayStoreLocale().toString()
    else -> this.language.code
}

fun Details.Movie?.youtubeTrailer(): Details.Movie.VideoResult.Video? {
    val locale = Locale.default()

    return this?.youtubeTrailer(
        language = locale.language.code,
        country = locale.territory?.code ?: locale.language.defaultCountry.code
    )
}