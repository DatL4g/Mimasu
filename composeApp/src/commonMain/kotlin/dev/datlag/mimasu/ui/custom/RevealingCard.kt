package dev.datlag.mimasu.ui.custom

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun RevealingCard(
    isRevealed: Boolean,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = { },
    cardEnabled: Boolean = true,
    cardColors: CardColors = CardDefaults.cardColors(),
    revealedCardColors: CardColors = cardColors,
    actionsContent: @Composable RowScope.() -> Unit,
    cardContent: @Composable ColumnScope.() -> Unit
) {
    var actionsWidth by remember { mutableStateOf(0.dp) }
    val offsetX by animateDpAsState(
        targetValue = if (isRevealed) -actionsWidth else 0.dp,
        animationSpec = tween()
    )
    val density = LocalDensity.current

    Box(
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .onSizeChanged {
                    actionsWidth = with(density) { it.width.toDp() }
                },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            content = actionsContent
        )

        Card(
            onClick = onCardClick,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = offsetX),
            content = cardContent,
            enabled = cardEnabled,
            colors = if (isRevealed) revealedCardColors else cardColors
        )
    }
}