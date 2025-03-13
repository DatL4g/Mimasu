package dev.datlag.mimasu.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Copied from androidx.paging.compose
 */
actual class LazyPagingItems<T : Any>(
    private val flow: Flow<PagingData<T>>
) {
    private val mainDispatcher = Dispatchers.Main

    private val pagingDataPresenter = object : PagingDataPresenter<T>(
        mainContext = mainDispatcher,
        cachedPagingData = if (flow is SharedFlow<PagingData<T>>) flow.replayCache.firstOrNull() else null
    ) {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) {
            updateItemSnapshotList()
        }
    }

    actual var itemSnapshotList by mutableStateOf(
        pagingDataPresenter.snapshot()
    )
        private set

    actual val itemCount: Int
        get() = itemSnapshotList.size

    private fun updateItemSnapshotList() {
        itemSnapshotList = pagingDataPresenter.snapshot()
    }

    actual operator fun get(index: Int): T? {
        pagingDataPresenter[index]
        return itemSnapshotList[index]
    }

    actual fun peek(index: Int): T? {
        return itemSnapshotList[index]
    }

    actual fun retry() {
        pagingDataPresenter.retry()
    }

    actual fun refresh() {
        pagingDataPresenter.refresh()
    }

    private val loadStates = LoadStates(
        refresh = LoadState.Loading,
        prepend = LoadState.NotLoading(false),
        append = LoadState.NotLoading(false),
    )

    actual var loadState: CombinedLoadStates by mutableStateOf(
        pagingDataPresenter.loadStateFlow.value
            ?: CombinedLoadStates(
                refresh = loadStates.refresh,
                prepend = loadStates.prepend,
                append = loadStates.append,
                source = loadStates
            )
    )
        private set

    internal suspend fun collectLoadState() {
        pagingDataPresenter.loadStateFlow.filterNotNull().collect {
            loadState = it
        }
    }

    internal suspend fun collectPagingData() {
        flow.collectLatest {
            pagingDataPresenter.collectFrom(it)
        }
    }
}

/**
 * Copied from androidx.paging.compose
 */
@Composable
actual fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(
    context: CoroutineContext
): LazyPagingItems<T> {
    val lazyPagingItems = remember(this) { LazyPagingItems(this) }

    LaunchedEffect(lazyPagingItems) {
        if (context == EmptyCoroutineContext) {
            lazyPagingItems.collectPagingData()
        } else {
            withContext(context) {
                lazyPagingItems.collectPagingData()
            }
        }
    }

    LaunchedEffect(lazyPagingItems) {
        if (context == EmptyCoroutineContext) {
            lazyPagingItems.collectLoadState()
        } else {
            withContext(context) {
                lazyPagingItems.collectLoadState()
            }
        }
    }

    return lazyPagingItems
}