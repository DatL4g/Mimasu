package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.error_state_text
import dev.datlag.mimasu.ui.error_state_with_throwable_text
import dev.datlag.tooling.compose.LaunchedVirtualIO
import dev.datlag.tooling.compose.platform.PlatformText

@Composable
fun ErrorState(
    throwable: Throwable?,
    additionalInfo: String = "",
    modifier: Modifier = Modifier
) {
    val text = if (throwable != null) {
        uiStringRes(UiRes.string.error_state_with_throwable_text)
    } else {
        uiStringRes(UiRes.string.error_state_text)
    }

    LaunchedVirtualIO(throwable) {
        Logger.e(throwable) { additionalInfo }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        PlatformText(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}