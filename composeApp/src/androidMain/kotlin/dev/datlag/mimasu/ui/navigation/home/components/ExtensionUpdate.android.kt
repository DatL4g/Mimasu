package dev.datlag.mimasu.ui.navigation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_download
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_text
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_title
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_view
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.UpdateProvider
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import org.jetbrains.compose.resources.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
actual fun ExtensionUpdate(
    modifier: Modifier
) = with(localDI()) {
    val provider by instanceOrNull<UpdateProvider>()
    val safeProvider = provider ?: ExtensionInitializer.getUpdateProvider(LocalContext.current)
    val update by safeProvider.update.collectAsStateWithLifecycle()

    if (update != null && update?.available == true) {
        ElevatedCard(
            modifier = modifier,
            onClick = {

            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.home_extension_update_title),
                    style = Platform.typography().headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = stringResource(Res.string.home_extension_update_text))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val uriHandler = LocalUriHandler.current

                    update?.downloadUrl?.let {
                        Button(
                            onClick = { }
                        ) {
                            MaterialSymbols(
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                name = MaterialSymbols.DOWNLOAD,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = stringResource(Res.string.home_extension_update_download))
                        }
                    }
                    update?.viewUrl?.let {
                        Button(
                            onClick = {
                                uriHandler.openUri(it)
                            }
                        ) {
                            MaterialSymbols(
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                name = MaterialSymbols.OPEN_IN_BROWSER,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                            Text(text = stringResource(Res.string.home_extension_update_view))
                        }
                    }
                }
            }
        }
    }
}