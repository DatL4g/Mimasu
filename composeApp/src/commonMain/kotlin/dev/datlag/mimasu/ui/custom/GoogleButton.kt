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

@Composable
fun GoogleButton(
    onClick: () -> Unit,
    darkContainer: Boolean = !LocalDarkMode.current,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = ButtonDefaults.IconSize,
) {
    val containerColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFF131314)
        } else {
            Color(0xFFFFFFFF)
        }
    }
    val contentColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFFE3E3E3)
        } else {
            Color(0xFF1F1F1F)
        }
    }
    val border = remember(darkContainer) {
        if (darkContainer) {
            BorderStroke(
                width = 1.dp,
                color = Color(0xFF8E918F)
            )
        } else {
            BorderStroke(
                width = 1.dp,
                color = Color(0xFF747775)
            )
        }
    }

    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = MaterialSymbols.GoogleGLogo,
            contentDescription = null,
            tint = Color.Unspecified
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
fun GoogleIconButton(
    onClick: () -> Unit,
    darkContainer: Boolean = !LocalDarkMode.current,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = ButtonDefaults.IconSize,
) {
    val containerColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFF131314)
        } else {
            Color(0xFFFFFFFF)
        }
    }
    val contentColor = remember(darkContainer) {
        if (darkContainer) {
            Color(0xFFE3E3E3)
        } else {
            Color(0xFF1F1F1F)
        }
    }
    val border = remember(darkContainer) {
        if (darkContainer) {
            BorderStroke(
                width = 1.dp,
                color = Color(0xFF8E918F)
            )
        } else {
            BorderStroke(
                width = 1.dp,
                color = Color(0xFF747775)
            )
        }
    }

    IconButton(
        modifier = modifier.border(border, CircleShape),
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = MaterialSymbols.GoogleGLogo,
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}