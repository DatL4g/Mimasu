package dev.datlag.mimasu.core

import dev.datlag.tooling.scopeCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.KClass

@OptIn(ExperimentalJsReflectionCreateInstance::class)
actual infix fun <T : Any> KClass<T>.typeOf(base: KClass<*>): Boolean {
    return this == base || this.js == base.js || scopeCatching {
        val jsClass = this.js.asDynamic()
        val baseClass = base.js.asDynamic()

        js("jsClass.prototype instanceof baseClass") as? Boolean
    }.getOrNull() == true
}

actual val Dispatchers.Virtual: CoroutineDispatcher?
    get() = null