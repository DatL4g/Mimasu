package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import dev.datlag.tooling.compose.TargetDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun LaunchedDefault(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetDefault,
        key1 = key1,
        block = block
    )
}

@Composable
fun LaunchedDefault(
    key1: Any?,
    key2: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetDefault,
        key1 = key1,
        key2 = key2,
        block = block
    )
}

@Composable
fun LaunchedDefault(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetDefault,
        key1 = key1,
        key2 = key2,
        key3 = key3,
        block = block
    )
}

@Composable
fun LaunchedDefault(
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetDefault,
        keys = keys,
        block = block
    )
}
