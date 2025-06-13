package dev.datlag.mimasu.ui.navigation.home.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.update_extension_dialog_cancel
import dev.datlag.mimasu.composeapp.generated.resources.update_extension_dialog_confirm
import dev.datlag.mimasu.composeapp.generated.resources.update_extension_dialog_text_hint
import dev.datlag.mimasu.composeapp.generated.resources.update_extension_dialog_text_hint_text
import dev.datlag.mimasu.composeapp.generated.resources.update_extension_dialog_text_process
import dev.datlag.mimasu.composeapp.generated.resources.update_extension_dialog_title
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExtensionUpdateDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            MaterialSymbols(
                name = MaterialSymbols.APK_INSTALL,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(Res.string.update_extension_dialog_title)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().scrollable(
                    state = rememberScrollState(),
                    orientation = Orientation.Vertical
                )
            ) {
                Text(text = stringResource(Res.string.update_extension_dialog_text_process))
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(
                            fontWeight = FontWeight.Bold
                        )) {
                            append(stringResource(Res.string.update_extension_dialog_text_hint))
                        }
                        append(' ')
                        append(stringResource(Res.string.update_extension_dialog_text_hint_text))
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(Res.string.update_extension_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Platform.colorScheme().error
                )
            ) {
                Text(stringResource(Res.string.update_extension_dialog_cancel))
            }
        }
    )
}