package dev.datlag.mimasu.tv.ui.navigation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.datlag.mimasu.tv.ui.custom.Keyboard
import dev.datlag.mimasu.ui.viewmodel.SearchViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel

@Composable
fun Search(paddingValues: PaddingValues) {
    val layoutDirection = LocalLayoutDirection.current

    Row(
        modifier = Modifier
            .padding(start = paddingValues.calculateStartPadding(layoutDirection) + 32.dp)
            .padding(end = paddingValues.calculateEndPadding(layoutDirection) + 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val searchViewModel = kodeinViewModel<SearchViewModel>()
        val query by searchViewModel.query.collectAsState()

        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding() + 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier
                    .width(300.dp),
                text = query?.trim() ?: "Start searching...",
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Keyboard(
                modifier = Modifier
                    .width(300.dp),
                value = query ?: "",
                onValueChange = {
                    searchViewModel.updateQuery(it)
                }
            )
        }
        LazyVerticalGrid(
            columns = GridCells.FixedSize(120.dp),
            modifier = Modifier.weight(1F).fillMaxHeight(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {

        }
    }
}