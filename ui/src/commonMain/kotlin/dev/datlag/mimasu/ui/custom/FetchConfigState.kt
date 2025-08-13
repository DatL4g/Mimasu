package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.fetch_connecting_services
import dev.datlag.mimasu.ui.fetch_description
import dev.datlag.mimasu.ui.fetch_initialize_app
import dev.datlag.mimasu.ui.fetch_loading_data
import dev.datlag.mimasu.ui.fetch_title
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@OptIn(MaterialSymbols.RedrawRequired::class)
@Composable
fun FetchConfigState() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.asPaddingValues(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.aligned(Alignment.CenterVertically)
    ) {
        item {
            PlatformText(
                modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                text = uiStringRes(UiRes.string.fetch_title),
                style = Platform.typography().headlineMedium,
                textAlign = TextAlign.Center
            )
        }
        item {
            PlatformText(
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
                text = uiStringRes(UiRes.string.fetch_description),
                textAlign = TextAlign.Center,
                softWrap = true
            )
        }
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MaterialSymbols.forcedRedraw(
                    name = MaterialSymbols.CLOUD_DOWNLOAD,
                    contentDescription = null,
                )
                PlatformText(text = uiStringRes(UiRes.string.fetch_loading_data))
            }
        }
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MaterialSymbols.forcedRedraw(
                    name = MaterialSymbols.SETTINGS_ETHERNET,
                    contentDescription = null,
                )
                PlatformText(text = uiStringRes(UiRes.string.fetch_connecting_services))
            }
        }
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MaterialSymbols.forcedRedraw(
                    name = MaterialSymbols.PLAY_CIRCLE,
                    contentDescription = null,
                )
                PlatformText(text = uiStringRes(UiRes.string.fetch_initialize_app))
            }
        }
    }
}