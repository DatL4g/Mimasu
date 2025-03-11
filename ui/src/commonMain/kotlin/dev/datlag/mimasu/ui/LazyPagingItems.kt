package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import androidx.paging.CombinedLoadStates
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

expect class LazyPagingItems<T : Any> {
    var itemSnapshotList: ItemSnapshotList<T>
        private set

    val itemCount: Int

    var loadState: CombinedLoadStates
        private set

    operator fun get(index: Int): T?
    fun peek(index: Int): T?
    fun retry()
    fun refresh()
}

@Composable
expect fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(
    context: CoroutineContext = EmptyCoroutineContext
): LazyPagingItems<T>