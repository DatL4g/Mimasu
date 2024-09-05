package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.LocalDarkMode
import dev.datlag.mimasu.common.githubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.ifTrue

@Composable
fun GitHubButton(
    onClick: (GitHubAuthParams) -> Unit,
    darkContainer: Boolean = !LocalDarkMode.current,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = ButtonDefaults.IconSize,
    authParams: GitHubAuthParams? = Platform.githubAuthParams()
) {
    val params = authParams ?: Platform.githubAuthParams() ?: return
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

    Button(
        modifier = modifier,
        onClick = { onClick(params) },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = MaterialSymbols.Github,
            contentDescription = null
        )
        Spacer(modifier = Modifier.defaultMinSize(minWidth = ButtonDefaults.IconSpacing).weight(1F))
        Text(
            text = text,
            maxLines = 1
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
    authParams: GitHubAuthParams? = Platform.githubAuthParams()
) {
    val params = authParams ?: Platform.githubAuthParams() ?: return
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

    IconButton(
        modifier = modifier.border(border ?: BorderStroke(0.dp, Color.Unspecified), CircleShape),
        onClick = {
            onClick(params)
        },
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = MaterialSymbols.Github,
            contentDescription = null
        )
    }
}