package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.core.Virtual
import dev.datlag.tooling.compose.TargetIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun LaunchedVirtualIO(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.Virtual ?: Dispatchers.TargetIO,
        key1 = key1,
        block = block
    )
}

@Composable
fun LaunchedVirtualIO(
    key1: Any?,
    key2: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.Virtual ?: Dispatchers.TargetIO,
        key1 = key1,
        key2 = key2,
        block = block
    )
}

@Composable
fun LaunchedVirtualIO(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.Virtual ?: Dispatchers.TargetIO,
        key1 = key1,
        key2 = key2,
        key3 = key3,
        block = block
    )
}

@Composable
fun LaunchedVirtualIO(
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.Virtual ?: Dispatchers.TargetIO,
        keys = keys,
        block = block
    )
}
