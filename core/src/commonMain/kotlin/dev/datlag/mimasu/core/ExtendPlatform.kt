package dev.datlag.mimasu.core

import kotlin.reflect.KClass

expect infix fun <T : Any> KClass<T>.typeOf(base: KClass<*>): Boolean