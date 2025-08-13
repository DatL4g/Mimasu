package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_header
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_info_close
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_text
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExtensionInfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            MaterialSymbols(
                name = MaterialSymbols.EXTENSION,
                contentDescription = null,
                filled = true
            )
        },
        title = {
            Text(text = stringResource(Res.string.profile_extension_header))
        },
        text = {
            Text(text = stringResource(Res.string.profile_extension_text))
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(text = stringResource(Res.string.profile_extension_info_close))
            }
        }
    )
}