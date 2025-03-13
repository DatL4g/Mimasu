package dev.datlag.mimasu.ui

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmSuppressWildcards

/**
 * Copied from androidx.paging.compose
 */
actual fun <T : Any> LazyPagingItems<T>.itemKey(
    key: ((item: @JvmSuppressWildcards T) -> Any)?
): (index: Int) -> Any {
    return { index ->
        if (key == null) {
            getPagingPlaceholderKey(index)
        } else {
            val item = peek(index)
            if (item == null) getPagingPlaceholderKey(index) else key(item)
        }
    }
}

/**
 * Copied from androidx.paging.compose
 */
actual fun <T : Any> LazyPagingItems<T>.itemContentType(
    contentType: ((item: @JvmSuppressWildcards T) -> Any?)?
): (index: Int) -> Any? {
    return { index ->
        if (contentType == null) {
            null
        } else {
            val item = peek(index)
            if (item == null) PagingPlaceholderContentType else contentType(item)
        }
    }
}

/**
 * Copied from androidx.paging.compose
 */
internal fun getPagingPlaceholderKey(index: Int): Any = PagingPlaceholderKey(index)

/**
 * Copied from androidx.paging.compose
 *
 * Using [Serializable] instead of `Parcelable`
 */
@Serializable
private data class PagingPlaceholderKey(private val index: Int)

/**
 * Copied from androidx.paging.compose
 */
internal object PagingPlaceholderContentType