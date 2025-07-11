package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProduceStateScope
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@Composable
fun <T> produceCoroutineState(
    initialValue: T,
    coroutineContext: CoroutineContext,
    producer: suspend ProduceStateScope<T>.() -> Unit,
): State<T> {
    return produceState(
        initialValue = initialValue,
    ) {
        withContext(coroutineContext) {
            producer()
        }
    }
}

@Composable
fun <T> produceCoroutineState(
    initialValue: T,
    coroutineContext: CoroutineContext,
    key1: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit,
): State<T> {
    return produceState(
        initialValue = initialValue,
        key1 = key1
    ) {
        withContext(coroutineContext) {
            producer()
        }
    }
}

@Composable
fun <T> produceCoroutineState(
    initialValue: T,
    coroutineContext: CoroutineContext,
    key1: Any?,
    key2: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit,
): State<T> {
    return produceState(
        initialValue = initialValue,
        key1 = key1,
        key2 = key2
    ) {
        withContext(coroutineContext) {
            producer()
        }
    }
}

@Composable
fun <T> produceCoroutineState(
    initialValue: T,
    coroutineContext: CoroutineContext,
    key1: Any?,
    key2: Any?,
    key3: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit,
): State<T> {
    return produceState(
        initialValue = initialValue,
        key1 = key1,
        key2 = key2,
        key3 = key3
    ) {
        withContext(coroutineContext) {
            producer()
        }
    }
}

@Composable
fun <T> produceCoroutineState(
    initialValue: T,
    coroutineContext: CoroutineContext,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit,
): State<T> {
    return produceState(
        initialValue = initialValue,
        keys = keys
    ) {
        withContext(coroutineContext) {
            producer()
        }
    }
}