package dev.datlag.mimasu.ui.space.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.formatBytesHumanReadable
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.space_sizes_app
import dev.datlag.mimasu.composeapp.generated.resources.space_sizes_cache
import dev.datlag.mimasu.composeapp.generated.resources.space_sizes_user
import dev.datlag.mimasu.other.SpaceManager
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@Composable
fun SizeInfo(
    sizes: SpaceManager.Sizes,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sizes.app.takeIf { it > 0 }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlatformText(
                    text = stringResource(Res.string.space_sizes_app),
                    style = Platform.typography().labelSmall
                )
                PlatformText(
                    text = it.formatBytesHumanReadable(),
                    style = Platform.typography().titleLarge
                )
            }
        }
        sizes.userData.takeIf { it > 0 }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlatformText(
                    text = stringResource(Res.string.space_sizes_user),
                    style = Platform.typography().labelSmall
                )
                PlatformText(
                    text = it.formatBytesHumanReadable(),
                    style = Platform.typography().titleLarge
                )
            }
        }
        sizes.cache.takeIf { it > 0 }?.let {
            Column(
                modifier = Modifier.weight(1F),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlatformText(
                    text = stringResource(Res.string.space_sizes_cache),
                    style = Platform.typography().labelSmall
                )
                PlatformText(
                    text = it.formatBytesHumanReadable(),
                    style = Platform.typography().titleLarge
                )
            }
        }
    }
}