package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_owner
import dev.datlag.mimasu.core.Constants
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.compose.platform.shapes
import io.tolgee.stringResource

@Composable
fun OwnerSection(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = ButtonDefaults.MinHeight, minWidth = ButtonDefaults.MinWidth)
            .clip(Platform.shapes().medium)
            .onClick {
                uriHandler.openUri(Constants.GITHUB_OWNER)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MaterialSymbols(
            name = MaterialSymbols.CODE,
            contentDescription = null
        )
        Text(
            text = stringResource(Res.string.profile_owner),
            maxLines = 2,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
    }
}