package dev.datlag.mimasu.ui

import kotlin.jvm.JvmSuppressWildcards

expect fun <T : Any> LazyPagingItems<T>.itemKey(
    key: ((item: @JvmSuppressWildcards T) -> Any)? = null
): (index: Int) -> Any

expect fun <T : Any> LazyPagingItems<T>.itemContentType(
    contentType: ((item: @JvmSuppressWildcards T) -> Any?)? = null
): (index: Int) -> Any?