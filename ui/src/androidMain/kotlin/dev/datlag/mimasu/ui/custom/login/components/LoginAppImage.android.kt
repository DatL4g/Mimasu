package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.core.Constants
import dev.datlag.mimasu.rive.RiveAnimation
import dev.datlag.mimasu.ui.AppInitializer
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.supportsRive
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.login_rive_bunny_license
import dev.datlag.mimasu.ui.login_rive_bunny_marketplace
import dev.datlag.mimasu.ui.login_rive_bunny_owner
import dev.datlag.mimasu.ui.login_rive_bunny_text
import dev.datlag.mimasu.ui.login_rive_bunny_title
import dev.datlag.tooling.compose.LaunchedVirtualIO
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal actual fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imagePainter: Painter,
    imageModifier: Modifier,
    riveModifier: Modifier
) {
    val context = LocalContext.current
    var fallback by remember { mutableStateOf(false) }

    if (fallback || !context.supportsRive()) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    } else {
        var bytes by rememberSaveable {
            mutableStateOf(ByteArray(0))
        }
        val riveLoaded = remember(context) {
            AppInitializer.isRiveLoaded(context)
        }

        LaunchedVirtualIO(bytes) {
            if (bytes.isEmpty()) {
                bytes = UiRes.readBytes("files/rive/bunny_login.riv")
            }
        }

        if (bytes.isEmpty() || !riveLoaded) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        } else {
            val tooltipState = rememberTooltipState()

            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                state = tooltipState,
                tooltip = {
                    RichTooltip(
                        caretShape = TooltipDefaults.caretShape(DpSize(32.dp, 16.dp)),
                        title = {
                            Text(uiStringRes(UiRes.string.login_rive_bunny_title))
                        },
                        text = {
                            Text(
                                text = uiStringRes(
                                    resource = UiRes.string.login_rive_bunny_text,
                                    uiStringRes(
                                        resource = UiRes.string.login_rive_bunny_owner
                                    )
                                )
                            )
                        },
                        action = {
                            val uriHandler = LocalUriHandler.current

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        uriHandler.openUri(Constants.RIVE_BUNNY_LINK)
                                    },
                                    shapes = ButtonDefaults.shapes()
                                ) {
                                    Text(uiStringRes(UiRes.string.login_rive_bunny_marketplace))
                                }
                                TextButton(
                                    onClick = {
                                        uriHandler.openUri(Constants.CREATIVE_COMMONS_4_LICENSE)
                                    },
                                    shapes = ButtonDefaults.shapes()
                                ) {
                                    Text(uiStringRes(UiRes.string.login_rive_bunny_license))
                                }
                            }
                        }
                    )
                }
            ) {
                RiveAnimation(
                    bytes = bytes,
                    modifier = riveModifier,
                    onUnavailable = {
                        fallback = true
                    }
                ) { state ->

                    state.setBoolean(
                        stateMachineName = "State Machine 1",
                        inputName = "isFocus",
                        value = typingEmail
                    )

                    state.setBoolean(
                        stateMachineName = "State Machine 1",
                        inputName = "IsPassword",
                        value = typingPassword
                    )
                }
            }
        }
    }
}