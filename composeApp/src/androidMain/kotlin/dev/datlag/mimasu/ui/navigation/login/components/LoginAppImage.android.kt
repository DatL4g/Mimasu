package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.AppInitializer
import dev.datlag.mimasu.R
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.login_rive_bunny_license
import dev.datlag.mimasu.composeapp.generated.resources.login_rive_bunny_marketplace
import dev.datlag.mimasu.composeapp.generated.resources.login_rive_bunny_owner
import dev.datlag.mimasu.composeapp.generated.resources.login_rive_bunny_text
import dev.datlag.mimasu.composeapp.generated.resources.login_rive_bunny_title
import dev.datlag.mimasu.core.Constants
import dev.datlag.mimasu.rive.RiveAnimation
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun LoginAppImage(
    typingEmail: Boolean,
    typingPassword: Boolean,
    imageModifier: Modifier,
    riveModifier: Modifier
) {
    val context = LocalContext.current
    var bytes by rememberSaveable {
        mutableStateOf(ByteArray(0))
    }
    val riveLoaded = remember(context) {
        AppInitializer.isRiveLoaded(context)
    }

    LaunchedEffect(bytes) {
        if (bytes.isEmpty()) {
            bytes = Res.readBytes("files/rive/bunny_login.riv")
        }
    }

    if (bytes.isEmpty() || !riveLoaded) {
        val image = AnimatedImageVector.animatedVectorResource(R.drawable.animated_launcher)

        Image(
            painter = rememberAnimatedVectorPainter(image, false),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    } else {
        val tooltipState = rememberTooltipState()

        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            state = tooltipState,
            tooltip = {
                RichTooltip(
                    caretSize = DpSize(32.dp, 16.dp),
                    title = {
                        Text(stringResource(Res.string.login_rive_bunny_title))
                    },
                    text = {
                        Text(stringResource(Res.string.login_rive_bunny_text, stringResource(Res.string.login_rive_bunny_owner)))
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
                                }
                            ) {
                                Text(stringResource(Res.string.login_rive_bunny_marketplace))
                            }
                            TextButton(
                                onClick = {
                                    uriHandler.openUri(Constants.CREATIVE_COMMONS_4_LICENSE)
                                }
                            ) {
                                Text(stringResource(Res.string.login_rive_bunny_license))
                            }
                        }
                    }
                )
            }
        ) {
            RiveAnimation(
                bytes = bytes,
                modifier = riveModifier
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