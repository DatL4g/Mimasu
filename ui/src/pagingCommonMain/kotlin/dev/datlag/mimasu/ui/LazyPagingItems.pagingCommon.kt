package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

actual typealias LazyPagingItems<T> = app.cash.paging.compose.LazyPagingItems<T>

@Composable
actual fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(
    context: CoroutineContext
): LazyPagingItems<T> = collectAsLazyPagingItems(context)