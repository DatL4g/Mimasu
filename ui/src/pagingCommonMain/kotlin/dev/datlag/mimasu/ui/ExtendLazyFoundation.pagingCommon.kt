package dev.datlag.mimasu.ui

import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import kotlin.jvm.JvmSuppressWildcards

actual fun <T : Any> LazyPagingItems<T>.itemKey(
    key: ((item: @JvmSuppressWildcards T) -> Any)?
): (index: Int) -> Any = itemKey(key)

actual fun <T : Any> LazyPagingItems<T>.itemContentType(
    contentType: ((item: @JvmSuppressWildcards T) -> Any?)?
): (index: Int) -> Any? = itemContentType(contentType)