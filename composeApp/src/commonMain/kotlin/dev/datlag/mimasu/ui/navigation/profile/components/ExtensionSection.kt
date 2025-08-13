package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import io.tolgee.stringResource

@Composable
fun ExtensionSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MaterialSymbols(
            name = MaterialSymbols.EXTENSION,
            contentDescription = null,
            filled = true
        )
        Text(
            text = stringResource(Res.string.profile_extension)
        )
        Spacer(modifier = Modifier.weight(1f))
        ExtensionButton()
    }
}

@Composable
expect fun ExtensionButton()