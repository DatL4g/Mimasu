package dev.datlag.mimasu.tv

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.material3.Text
import dev.datlag.mimasu.tv.common.not
import dev.datlag.mimasu.tv.common.rememberDirectionAwareKeyboardAlignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
actual fun TvTest() {
    val alignment = rememberDirectionAwareKeyboardAlignment()
    val data by counter.collectAsStateWithLifecycle(initialValue = 0)
    Column {
        Text(text = "Direction Keyboard: ${alignment.option}")
        Text(text = "Direction Keyboard: ${alignment.not().option}")
        Text(text = "Counter: $data")
    }
}

val counter = flow {
    var count = 0
    while (count < 5) {
        emit(++count)
        delay(1000)
    }
}