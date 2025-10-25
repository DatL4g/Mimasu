package dev.datlag.mimasu.ui.space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.app_name
import dev.datlag.mimasu.other.SpaceManager
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.space.components.SizeInfo
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.launchVirtualIO
import dev.datlag.tooling.compose.LaunchedVirtualIO
import dev.datlag.tooling.compose.platform.PlatformButton
import dev.datlag.tooling.compose.platform.PlatformButtonColors
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@OptIn(MaterialSymbols.RedrawRequired::class)
@Composable
fun SpaceContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        val context = LocalContext.current
        val spaceManager = remember(context) { SpaceManager(context) }
        val scope = rememberCoroutineScope()
        val sizes by if (Platform.rememberIsTv()) {
            spaceManager.sizes.collectAsState()
        } else {
            spaceManager.sizes.collectAsStateWithLifecycle()
        }

        LaunchedVirtualIO(spaceManager) {
            spaceManager.loadSizes()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            item {
                PlatformText(
                    modifier = Modifier.fillParentMaxWidth().padding(16.dp),
                    text = stringResource(Res.string.app_name),
                    style = Platform.typography().headlineLarge,
                    maxLines = 1
                )
            }
            item {
                PlatformText(
                    modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                    text = "Free up space by clearing the cache or permanently deleting all data."
                )
            }
            if (sizes != null) {
                item {
                    SizeInfo(
                        sizes = sizes!!,
                        modifier = Modifier.fillParentMaxWidth().padding(16.dp)
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlatformButton(
                        modifier = Modifier.weight(1F),
                        onClick = {
                            scope.launchVirtualIO {
                                spaceManager.clearApplicationData()
                            }
                        },
                        colors = PlatformButtonColors.default(
                            containerColor = Platform.colorScheme().error,
                            contentColor = Platform.colorScheme().onError
                        )
                    ) {
                        MaterialSymbols.forcedRedraw(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = MaterialSymbols.DELETE_FOREVER,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        PlatformText(text = "Clear Storage")
                    }

                    PlatformButton(
                        modifier = Modifier.weight(1F),
                        onClick = {
                            scope.launchVirtualIO {
                                spaceManager.clearCache()
                            }
                        }
                    ) {
                        MaterialSymbols.forcedRedraw(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = MaterialSymbols.DELETE,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        PlatformText(text = "Clear Cache")
                    }
                }
            }
        }
    }
}