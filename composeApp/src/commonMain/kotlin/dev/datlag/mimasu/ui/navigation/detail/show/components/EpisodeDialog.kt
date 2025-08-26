package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_info_close
import dev.datlag.mimasu.composeapp.generated.resources.show_episode_dialog_more
import dev.datlag.mimasu.composeapp.generated.resources.show_episode_dialog_text
import dev.datlag.mimasu.composeapp.generated.resources.show_episode_dialog_title
import dev.datlag.mimasu.core.Constants
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EpisodeDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            MaterialSymbols(
                name = MaterialSymbols.ANIMATED_IMAGES,
                contentDescription = null
            )
        },
        title = {
            Text(text = stringResource(Res.string.show_episode_dialog_title))
        },
        text = {
            Text(text = stringResource(Res.string.show_episode_dialog_text))
        },
        dismissButton = {
            val uriHandler = LocalUriHandler.current

            TextButton(
                onClick = {
                    uriHandler.openUri(Constants.HOMEPAGE_MIMASU)
                },
                shapes = ButtonDefaults.shapes()
            ) {
                Text(text = stringResource(Res.string.show_episode_dialog_more))
            }
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