package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun ScrollIconButton(
    listState: LazyListState,
    alignment: Alignment.Horizontal,
    maxScrollItem: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    IconButton(
        onClick = {
            val target = when (alignment) {
                Alignment.Start -> {
                    (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                }
                Alignment.End -> {
                    (listState.firstVisibleItemIndex + 1).coerceAtMost(maxScrollItem)
                }
                else -> { listState.firstVisibleItemIndex }
            }

            scope.launch { listState.animateScrollToItem(target) }
        },
        modifier = modifier,
        content = content
    )
}
