package dev.datlag.mimasu.core

import kotlin.reflect.KClass

actual fun <T : Any> KClass<T>.typeOf(base: KClass<*>): Boolean {
    return this == base
}