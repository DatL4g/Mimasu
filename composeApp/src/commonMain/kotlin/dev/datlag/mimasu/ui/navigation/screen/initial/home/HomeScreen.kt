package dev.datlag.mimasu.ui.navigation.screen.initial.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.vanniktech.locale.Locale
import com.vanniktech.locale.Locales
import dev.datlag.mimasu.common.default
import dev.datlag.mimasu.common.localized
import dev.datlag.mimasu.tmdb.model.TrendingWindow
import dev.datlag.tooling.compose.platform.PlatformButton
import dev.datlag.tooling.compose.platform.PlatformText

@Composable
fun HomeScreen(component: HomeComponent) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val locale = Locales.currentLocaleString()

        Text(text = "Locale: $locale")
        Text(text = "Locales: ${Locales.currentLocaleStrings().joinToString()}")
        Text(text = "Google Play: ${Locale.from(locale).googlePlayStoreLocale()?.toString()}")
        Text(text = "Apple App Store: ${Locale.from(locale).appleAppStoreLocale()?.toString()}")
        Text(text = "Localized: ${Locale.default().localized()}")
        Text(text = "Window: ${TrendingWindow.Day::class == TrendingWindow::class}")
        Text(text = "Window Type Check: ${TrendingWindow.isTypeOf(TrendingWindow.Day::class)}")

        PlatformButton(
            onClick = {}
        ) {
            PlatformText(text = "Primary Button")
        }
    }
}