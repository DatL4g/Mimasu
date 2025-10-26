package dev.datlag.mimasu.ui.space.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.space_clear_cache
import dev.datlag.mimasu.composeapp.generated.resources.space_clear_space
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.launchVirtualIO
import dev.datlag.tooling.compose.platform.PlatformButton
import dev.datlag.tooling.compose.platform.PlatformButtonColors
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import io.tolgee.stringResource

@OptIn(MaterialSymbols.RedrawRequired::class)
@Composable
fun ClearRow(
    onClearStorage: () -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlatformButton(
            modifier = Modifier.weight(1F),
            onClick = onClearStorage,
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
            PlatformText(text = stringResource(Res.string.space_clear_space))
        }

        PlatformButton(
            modifier = Modifier.weight(1F),
            onClick = onClearCache
        ) {
            MaterialSymbols.forcedRedraw(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                name = MaterialSymbols.DELETE,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            PlatformText(text = stringResource(Res.string.space_clear_cache))
        }
    }
}