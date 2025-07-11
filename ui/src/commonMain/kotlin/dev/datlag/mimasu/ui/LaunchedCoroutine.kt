package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@Composable
fun LaunchedCoroutine(
    context: CoroutineContext,
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(key1 = key1) {
        withContext(context) {
            block()
        }
    }
}

@Composable
fun LaunchedCoroutine(
    context: CoroutineContext,
    key1: Any?,
    key2: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(key1 = key1, key2 = key2) {
        withContext(context) {
            block()
        }
    }
}

@Composable
fun LaunchedCoroutine(
    context: CoroutineContext,
    key1: Any?,
    key2: Any?,
    key3: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(key1 = key1, key2 = key2, key3 = key3) {
        withContext(context) {
            block()
        }
    }
}

@Composable
fun LaunchedCoroutine(
    context: CoroutineContext,
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedEffect(
        keys = keys
    ) {
        withContext(context) {
            block()
        }
    }
}
