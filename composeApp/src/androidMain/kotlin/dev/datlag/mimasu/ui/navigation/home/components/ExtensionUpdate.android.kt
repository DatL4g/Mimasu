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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_downloading
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_installing
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_text
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_title
import dev.datlag.mimasu.composeapp.generated.resources.home_extension_update_view
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.UpdateProvider
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.viewmodel.ExtensionUpdateViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.localContentColor
import dev.datlag.tooling.compose.platform.typography
import org.jetbrains.compose.resources.stringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
actual fun ExtensionUpdate(
    modifier: Modifier
) = with(localDI()) {
    val provider by instanceOrNull<UpdateProvider>()
    val context = LocalContext.current
    val safeProvider = provider ?: ExtensionInitializer.getUpdateProvider(context)
    val update by safeProvider.update.collectAsStateWithLifecycle()
    val extensionUpdateViewModel = kodeinViewModel<ExtensionUpdateViewModel>()

    if (update != null && update?.available == true) {
        ElevatedCard(
            modifier = modifier
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
                    val state by extensionUpdateViewModel.state.collectAsStateWithLifecycle()

                    LaunchedEffect(state) {
                        if (state is ExtensionUpdateViewModel.State.Install.Success) {
                            extensionUpdateViewModel.clearState()
                        }

                        ExtensionInitializer.rebindAll(context)
                    }

                    update?.downloadUrl?.let {
                        Button(
                            onClick = {
                                extensionUpdateViewModel.update(update)
                            },
                            enabled = state == null
                        ) {
                            when (val current = state) {
                                is ExtensionUpdateViewModel.State.Downloading.Unknown -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        strokeWidth = 2.dp,
                                        color = Platform.localContentColor()
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = stringResource(Res.string.home_extension_update_downloading))
                                }
                                is ExtensionUpdateViewModel.State.Downloading.Progress -> {
                                    CircularProgressIndicator(
                                        progress = { current.percentage },
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        strokeWidth = 2.dp,
                                        color = Platform.localContentColor()
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = stringResource(Res.string.home_extension_update_downloading))
                                }
                                is ExtensionUpdateViewModel.State.Install -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        strokeWidth = 2.dp,
                                        color = Platform.localContentColor()
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = stringResource(Res.string.home_extension_update_installing))
                                }
                                else -> {
                                    MaterialSymbols(
                                        modifier = Modifier.size(ButtonDefaults.IconSize),
                                        name = MaterialSymbols.DOWNLOAD,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(text = stringResource(Res.string.home_extension_update_download))
                                }
                            }
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