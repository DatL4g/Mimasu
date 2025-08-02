package dev.datlag.mimasu.core

import dev.datlag.tooling.scopeCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

actual infix fun <T : Any> KClass<T>.typeOf(base: KClass<*>): Boolean {
    return scopeCatching {
        this == base
    }.getOrNull() == true || scopeCatching {
        this.isSubclassOf(base)
    }.getOrNull() == true
}

private val virtualDispatcher: CoroutineDispatcher? by lazy {
    scopeCatching {
        Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    }.getOrNull() ?: scopeCatching {
        (Executors::class.java.getMethod("newVirtualThreadPerTaskExecutor").invoke(null) as? ExecutorService)?.asCoroutineDispatcher()
    }.getOrNull()
}

actual val Dispatchers.Virtual: CoroutineDispatcher?
    get() = virtualDispatcher