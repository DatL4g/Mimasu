package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.localContentColor
import io.tolgee.stringResource
import org.jetbrains.compose.resources.StringResource

@Composable
internal fun LoginPasswordCriteria(
    fulfilled: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MaterialSymbols(
            name = if (fulfilled) {
                MaterialSymbols.CHECK_SMALL
            } else {
                MaterialSymbols.CLOSE_SMALL
            },
            contentDescription = null,
            tint = if (fulfilled) Platform.localContentColor() else Platform.colorScheme().error
        )
        PlatformText(
            text = text,
            color = if (fulfilled) Platform.localContentColor() else Platform.colorScheme().error
        )
    }
}

@Composable
internal fun LoginPasswordCriteria(
    fulfilled: Boolean,
    text: StringResource,
    modifier: Modifier = Modifier
) = LoginPasswordCriteria(
    fulfilled = fulfilled,
    text = uiStringRes(text),
    modifier = modifier
)