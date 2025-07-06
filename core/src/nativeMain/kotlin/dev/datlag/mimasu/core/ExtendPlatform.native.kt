package dev.datlag.mimasu.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

actual infix fun <T : Any> KClass<T>.typeOf(base: KClass<*>): Boolean {
    return this == base
}

actual val Dispatchers.Virtual: CoroutineDispatcher?
    get() = null