package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.ui.LocalDarkMode
import dev.datlag.mimasu.ui.common.rememberGitHubAuthParams
import dev.datlag.tooling.compose.platform.PlatformBorder
import dev.datlag.tooling.compose.platform.PlatformButton
import dev.datlag.tooling.compose.platform.PlatformButtonBorder
import dev.datlag.tooling.compose.platform.PlatformButtonColors
import dev.datlag.tooling.compose.platform.PlatformButtonScale
import dev.datlag.tooling.compose.platform.PlatformIcon
import dev.datlag.tooling.compose.platform.PlatformIconButton
import dev.datlag.tooling.compose.platform.PlatformText

@Composable
fun GitHubButton(
    onClick: (GitHubAuthParams) -> Unit,
    darkContainer: Boolean = !LocalDarkMode.current,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = ButtonDefaults.IconSize,
    authParams: GitHubAuthParams? = rememberGitHubAuthParams()
) {
    val params = authParams ?: rememberGitHubAuthParams() ?: return
    val containerColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFF24292e)
        } else {
            Color(0xFF2b3137)
        }
    }
    val contentColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFFfafbfc)
        } else {
            Color(0xFFFFFFFF)
        }
    }
    val border = remember(enabled, darkContainer) {
        if (!enabled || darkContainer) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = contentColor.copy(alpha = 0.5F)
            )
        }
    }

    PlatformButton(
        modifier = modifier,
        onClick = { onClick(params) },
        enabled = enabled,
        colors = PlatformButtonColors.default(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = PlatformButtonBorder.default(
            border = if (border == null) {
                PlatformBorder.None
            } else {
                PlatformBorder(
                    border = border
                )
            }
        ),
        scale = PlatformButtonScale.default(
            scale = 1F,
            focusedScale = 1F
        )
    ) {
        PlatformIcon(
            modifier = Modifier.size(iconSize),
            imageVector = MaterialSymbols.Github,
            contentDescription = null
        )
        // Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Spacer(modifier = Modifier.defaultMinSize(minWidth = ButtonDefaults.IconSpacing).weight(1F))
        PlatformText(
            text = text,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1F))
    }
}

@Composable
fun GitHubIconButton(
    onClick: (GitHubAuthParams) -> Unit,
    darkContainer: Boolean = !LocalDarkMode.current,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = ButtonDefaults.IconSize,
    authParams: GitHubAuthParams? = rememberGitHubAuthParams()
) {
    val params = authParams ?: rememberGitHubAuthParams() ?: return
    val containerColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFF24292e)
        } else {
            Color(0xFF2b3137)
        }
    }
    val contentColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFFfafbfc)
        } else {
            Color(0xFFFFFFFF)
        }
    }
    val border = remember(enabled, darkContainer) {
        if (!enabled || darkContainer) {
            null
        } else {
            BorderStroke(
                width = 1.dp,
                color = contentColor.copy(alpha = 0.5F)
            )
        }
    }

    PlatformIconButton(
        modifier = modifier.border(border ?: BorderStroke(0.dp, Color.Unspecified), CircleShape),
        onClick = {
            onClick(params)
        },
        enabled = enabled,
        colors = PlatformButtonColors.icon(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        PlatformIcon(
            modifier = Modifier.size(iconSize),
            imageVector = MaterialSymbols.Github,
            contentDescription = null
        )
    }
}