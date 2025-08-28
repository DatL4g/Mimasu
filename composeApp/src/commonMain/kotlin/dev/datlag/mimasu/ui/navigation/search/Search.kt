package dev.datlag.mimasu.ui.navigation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.search_info_default
import dev.datlag.mimasu.composeapp.generated.resources.search_info_error
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.TV
import androidx.paging.compose.collectAsLazyPagingItems
import dev.datlag.mimasu.ui.common.merge
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.SearchInfo
import dev.datlag.mimasu.ui.custom.ShowCard
import dev.datlag.mimasu.ui.viewmodel.DiscoverViewModel
import dev.datlag.mimasu.ui.viewmodel.SearchViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Search(
    onPersonClicked: (People) -> Unit,
    onShowClicked: (TV) -> Unit,
    onMovieClicked: (Movie) -> Unit
) {
    val searchViewModel = kodeinViewModel<SearchViewModel>()
    val discoverViewModel = kodeinViewModel<DiscoverViewModel>()
    val query by searchViewModel.query.collectAsStateWithLifecycle()

    BackHandler(enabled = !query.isNullOrEmpty()) {
        searchViewModel.updateQuery("")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            SearchBar(
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query ?: "",
                        onQueryChange = {
                            searchViewModel.updateQuery(it)
                        },
                        onSearch = { },
                        expanded = false,
                        onExpandedChange = { },
                        leadingIcon = {
                            IconButton(
                                onClick = { }
                            ) {
                                MaterialSymbols(
                                    name = MaterialSymbols.SEARCH,
                                    contentDescription = null
                                )
                            }
                        },
                        trailingIcon = if (query.isNullOrEmpty()) null else {
                            {
                                IconButton(
                                    onClick = {
                                        searchViewModel.updateQuery("")
                                    }
                                ) {
                                    MaterialSymbols(
                                        name = MaterialSymbols.CLOSE,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    )
                },
                expanded = false,
                onExpandedChange = { },
                content = { }
            )
        }
    ) { padding ->
        val people = searchViewModel.people.collectAsLazyPagingItems()
        val movies = searchViewModel.movies.collectAsLazyPagingItems()
        val tv = searchViewModel.tv.collectAsLazyPagingItems()
        val isEmpty = people.itemCount <= 0 && movies.itemCount <= 0 && tv.itemCount <= 0
        val isError = people.loadState.hasError || movies.loadState.hasError || tv.loadState.hasError

        when {
            isEmpty && query.isNullOrEmpty() -> {
                val tvResult = discoverViewModel.discoverTV.collectAsLazyPagingItems()

                if (tvResult.itemCount <= 0) {
                    SearchInfo(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        iconName = MaterialSymbols.SEARCH,
                        text = stringResource(Res.string.search_info_default),
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(100.dp),
                        contentPadding = padding.merge(PaddingValues(horizontal = 8.dp)),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tvResult.itemCount) { index ->
                            ShowCard(
                                tv = tvResult[index],
                                onClick = onShowClicked
                            )
                        }
                    }
                }
            }
            isEmpty && isError -> {
                SearchInfo(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    iconName = MaterialSymbols.ERROR,
                    iconTint = Platform.colorScheme().error,
                    text = stringResource(Res.string.search_info_error),
                )
            }
            else -> {
                SearchContent(
                    padding = padding,
                    people = people,
                    movies = movies,
                    tv = tv,
                    onPersonClicked = onPersonClicked,
                    onMovieClicked = onMovieClicked,
                    onShowClicked = onShowClicked,
                )
            }
        }
    }
}