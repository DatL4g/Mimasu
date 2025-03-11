package dev.datlag.mimasu.ui

import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

actual fun <T : Any> LazyPagingItems<T>.itemKey(
    key: ((item: @JvmSuppressWildcards T) -> Any)?
): (index: Int) -> Any = itemKey(key)

actual fun <T : Any> LazyPagingItems<T>.itemContentType(
    contentType: ((item: @JvmSuppressWildcards T) -> Any?)?
): (index: Int) -> Any? = itemContentType(contentType)