package dev.datlag.mimasu.ui.space

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.app_name
import dev.datlag.mimasu.composeapp.generated.resources.space_app_text
import dev.datlag.mimasu.composeapp.generated.resources.space_extension
import dev.datlag.mimasu.composeapp.generated.resources.space_extension_text
import dev.datlag.mimasu.other.SpaceManager
import dev.datlag.mimasu.ui.ads.Banner
import dev.datlag.mimasu.ui.ads.BannerAd
import dev.datlag.mimasu.ui.common.findActivity
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.space.components.ClearRow
import dev.datlag.mimasu.ui.space.components.SizeInfo
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.launchVirtualIO
import dev.datlag.tooling.compose.LaunchedVirtualIO
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.scopeCatching
import io.tolgee.stringResource

@OptIn(MaterialSymbols.RedrawRequired::class)
@Composable
fun SpaceContent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!Platform.rememberIsTv()) {
                BannerAd(
                    type = Banner.Space,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        val context = LocalContext.current
        val activity = LocalActivity.current ?: context.findActivity()
        val spaceManager = remember(context) { SpaceManager(context) }
        val scope = rememberCoroutineScope()
        val sizes by if (Platform.rememberIsTv()) {
            spaceManager.sizes.collectAsState()
        } else {
            spaceManager.sizes.collectAsStateWithLifecycle()
        }
        val extensionAvailable = remember(spaceManager) {
            spaceManager.extensionAvailable
        }
        val _extensionSizes by if (Platform.rememberIsTv()) {
            spaceManager.extensionSizes.collectAsState()
        } else {
            spaceManager.extensionSizes.collectAsStateWithLifecycle()
        }
        val extensionSizes = remember(_extensionSizes) {
            _extensionSizes?.let {
                SpaceManager.Sizes(
                    _app = it.app,
                    _userData = it.user,
                    _cache = it.cache
                ).takeUnless { s -> s.isEmpty() }
            }
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
                    text = stringResource(Res.string.space_app_text)
                )
            }
            if (sizes != null) {
                item {
                    SizeInfo(
                        sizes = sizes!!,
                        modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp)
                    )
                }
            }
            item {
                ClearRow(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    onClearStorage = {
                        scope.launchVirtualIO {
                            spaceManager.clearApplicationData()
                            scopeCatching {
                                activity?.finishAffinity()
                            }.onFailure {
                                activity?.finish()
                            }
                        }
                    },
                    onClearCache = {
                        scope.launchVirtualIO {
                            spaceManager.clearCache()
                        }
                    }
                )
            }
            if (extensionAvailable) {
                item {
                    PlatformText(
                        modifier = Modifier.fillParentMaxWidth().padding(16.dp).padding(top = 32.dp),
                        text = stringResource(Res.string.space_extension),
                        style = Platform.typography().headlineLarge,
                        maxLines = 1
                    )
                }
                item {
                    PlatformText(
                        modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp),
                        text = stringResource(Res.string.space_extension_text)
                    )
                }
                if (extensionSizes != null) {
                    item {
                        SizeInfo(
                            sizes = extensionSizes,
                            modifier = Modifier.fillParentMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp)
                        )
                    }
                }
                item {
                    ClearRow(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        onClearStorage = {
                            spaceManager.extensionClearStorage()
                            scopeCatching {
                                activity?.finishAffinity()
                            }.onFailure {
                                activity?.finish()
                            }
                        },
                        onClearCache = {
                            spaceManager.extensionClearCache()
                        },
                        tonal = true
                    )
                }
            }
        }
    }
}