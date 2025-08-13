package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_info
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_open
import dev.datlag.mimasu.extension.AIDLService
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
            },
            shapes = ButtonDefaults.shapes()
        ) {
            Text(text = stringResource(Res.string.profile_extension_open))
        }
    } else {
        var dialog by remember { mutableStateOf(false) }

        if (dialog) {
            ExtensionInfoDialog(
                onDismiss = { dialog = false }
            )
        }

        Button(
            onClick = {
                dialog = !dialog
            },
            shapes = ButtonDefaults.shapes()
        ) {
            Text(text = stringResource(Res.string.profile_extension_info))
        }
    }
}