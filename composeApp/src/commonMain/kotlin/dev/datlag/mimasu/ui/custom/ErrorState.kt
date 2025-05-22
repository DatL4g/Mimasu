package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.error_state_text
import dev.datlag.mimasu.composeapp.generated.resources.error_state_with_throwable_text
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorState(
    throwable: Throwable?,
    additionalInfo: String = "",
    modifier: Modifier = Modifier
) {
    val text = if (throwable != null) {
        stringResource(Res.string.error_state_with_throwable_text)
    } else {
        stringResource(Res.string.error_state_text)
    }

    LaunchedEffect(throwable) {
        Logger.e(throwable) { additionalInfo }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}