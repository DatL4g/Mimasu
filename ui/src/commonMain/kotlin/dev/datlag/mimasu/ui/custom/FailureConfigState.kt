package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.core.Constants
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.clipEntryOf
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.config_fail_config_title
import dev.datlag.mimasu.ui.config_fail_connecting_services
import dev.datlag.mimasu.ui.config_fail_data
import dev.datlag.mimasu.ui.config_fail_description
import dev.datlag.mimasu.ui.config_fail_initialize_title
import dev.datlag.mimasu.ui.github
import dev.datlag.mimasu.ui.other.Network
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.launchMain
import dev.datlag.tooling.compose.platform.PlatformCard
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@OptIn(MaterialSymbols.RedrawRequired::class)
@Composable
fun FailureConfigState(
    state: Network.Config.Failure
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.asPaddingValues(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.aligned(Alignment.CenterVertically)
    ) {
        item {
            PlatformText(
                modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                text = when (state) {
                    is Network.Config.Failure.Initialize -> uiStringRes(UiRes.string.config_fail_initialize_title)
                    is Network.Config.Failure.Fetching -> uiStringRes(UiRes.string.config_fail_config_title)
                },
                style = Platform.typography().headlineMedium,
                textAlign = TextAlign.Center
            )
        }
        item {
            PlatformText(
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
                text = uiStringRes(UiRes.string.config_fail_description),
                textAlign = TextAlign.Center,
                softWrap = true
            )
        }
        (state as? Network.Config.Failure.Fetching)?.throwable?.message?.ifBlank { null }?.let { message ->
            item {
                val clipboardManager = LocalClipboard.current
                val scope = rememberCoroutineScope()

                PlatformCard(
                    onClick = {
                        scope.launchMain {
                            clipboardManager.setClipEntry(clipEntryOf(message))
                        }
                    },
                    modifier = Modifier.fillParentMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp)
                ) {
                    SelectionContainer {
                        PlatformText(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            textAlign = TextAlign.Center,
                            softWrap = true,
                            maxLines = 4,
                            text = message
                        )
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MaterialSymbols.forcedRedraw(
                    name = MaterialSymbols.CLOUD_OFF,
                    contentDescription = null,
                )
                PlatformText(text = uiStringRes(UiRes.string.config_fail_data))
            }
        }
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MaterialSymbols.forcedRedraw(
                    name = MaterialSymbols.WARNING,
                    contentDescription = null
                )
                PlatformText(text = uiStringRes(UiRes.string.config_fail_connecting_services))
            }
        }
        item {
            val uriHandler = LocalUriHandler.current

            GitHubButton(
                modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                onClick = {
                    uriHandler.openUri(Constants.GITHUB_REPOSITORY)
                },
                text = uiStringRes(UiRes.string.github)
            )
        }
    }
}