package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import dev.datlag.tooling.compose.TargetMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@RequiresOptIn(message = "Only use this if you really want to run a LaunchedEffect on main thread.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class MainThread

@MainThread
@Composable
fun LaunchedMain(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetMain.immediate,
        key1 = key1,
        block = block
    )
}

@MainThread
@Composable
fun LaunchedMain(
    key1: Any?,
    key2: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetMain.immediate,
        key1 = key1,
        key2 = key2,
        block = block
    )
}

@MainThread
@Composable
fun LaunchedMain(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetMain.immediate,
        key1 = key1,
        key2 = key2,
        key3 = key3,
        block = block
    )
}

@MainThread
@Composable
fun LaunchedMain(
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    LaunchedCoroutine(
        context = Dispatchers.TargetMain.immediate,
        keys = keys,
        block = block
    )
}
