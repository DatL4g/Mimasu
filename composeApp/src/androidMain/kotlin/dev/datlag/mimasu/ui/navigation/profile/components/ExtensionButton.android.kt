package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ExtensionButton() {
    val context = LocalContext.current
    val installed = remember(context) {
        AIDLService.extensionInstalled(context)
    }

    if (installed) {
        Button(
            onClick = {
                AIDLService.openExtension(context)
            }
        ) {
            MaterialSymbols(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                name = MaterialSymbols.EXTENSION,
                contentDescription = null,
                filled = true
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.profile_extension))
        }
    }
}