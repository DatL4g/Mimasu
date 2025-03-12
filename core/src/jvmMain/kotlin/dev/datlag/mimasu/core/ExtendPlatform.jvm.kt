package dev.datlag.mimasu.core

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

actual fun <T : Any> KClass<T>.typeOf(base: KClass<*>): Boolean {
    return this == base || this.isSubclassOf(base)
}