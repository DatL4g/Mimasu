package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

actual typealias LazyPagingItems<T> = androidx.paging.compose.LazyPagingItems<T>

@Composable
actual fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(
    context: CoroutineContext
): LazyPagingItems<T> = collectAsLazyPagingItems(context)