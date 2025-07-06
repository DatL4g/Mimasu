package dev.datlag.mimasu.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KClass

expect infix fun <T : Any> KClass<T>.typeOf(base: KClass<*>): Boolean

suspend inline fun <T> withNonEmptyContext(
    context: CoroutineContext,
    crossinline block: suspend CoroutineScope.() -> T
) = coroutineScope {
    if (context is EmptyCoroutineContext) {
        block()
    } else {
        withContext(context) {
            block()
        }
    }
}

expect val Dispatchers.Virtual: CoroutineDispatcher?