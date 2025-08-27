package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.datlag.tooling.compose.LaunchedCoroutine
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Composable
fun <T> rememberAdjustableState(
    initialValue: T,
    context: CoroutineContext = Dispatchers.Main.immediate,
    producer: suspend (current: T) -> T
): MutableState<T> {
    val state = remember { mutableStateOf(initialValue) }

    LaunchedCoroutine(context = context, key1 = Unit) {
        val result = producer(state.value)

        state.value = result
    }

    return state
}

@Composable
fun <T> rememberAdjustableState(
    initialValue: T,
    key1: Any?,
    context: CoroutineContext = Dispatchers.Main.immediate,
    producer: suspend (current: T) -> T
): MutableState<T> {
    val state = remember(key1 = key1) { mutableStateOf(initialValue) }

    LaunchedCoroutine(context = context, key1 = key1) {
        val result = producer(state.value)

        state.value = result
    }

    return state
}

@Composable
fun <T> rememberAdjustableState(
    initialValue: T,
    key1: Any?,
    key2: Any?,
    context: CoroutineContext = Dispatchers.Main.immediate,
    producer: suspend (current: T) -> T
): MutableState<T> {
    val state = remember(key1 = key1, key2 = key2) { mutableStateOf(initialValue) }

    LaunchedCoroutine(context = context, key1 = key1, key2 = key2) {
        val result = producer(state.value)

        state.value = result
    }

    return state
}

@Composable
fun <T> rememberAdjustableState(
    initialValue: T,
    key1: Any?,
    key2: Any?,
    key3: Any?,
    context: CoroutineContext = Dispatchers.Main.immediate,
    producer: suspend (current: T) -> T
): MutableState<T> {
    val state = remember(key1 = key1, key2 = key2, key3 = key3) { mutableStateOf(initialValue) }

    LaunchedCoroutine(context = context, key1 = key1, key2 = key2, key3 = key3) {
        val result = producer(state.value)

        state.value = result
    }

    return state
}

@Composable
fun <T> rememberAdjustableState(
    initialValue: T,
    context: CoroutineContext = Dispatchers.Main.immediate,
    vararg keys: Any?,
    producer: suspend (current: T) -> T
): MutableState<T> {
    val state = remember(keys = keys) { mutableStateOf(initialValue) }

    LaunchedCoroutine(context = context, keys = keys) {
        val result = producer(state.value)

        state.value = result
    }

    return state
}