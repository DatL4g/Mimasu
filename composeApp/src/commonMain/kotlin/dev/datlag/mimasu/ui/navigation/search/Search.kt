package dev.datlag.mimasu.ui.navigation.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import dev.datlag.mimasu.common.plus
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.home_people
import dev.datlag.mimasu.composeapp.generated.resources.movies_now_playing
import dev.datlag.mimasu.composeapp.generated.resources.search_movies
import dev.datlag.mimasu.composeapp.generated.resources.search_people
import dev.datlag.mimasu.composeapp.generated.resources.search_series
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.ui.collectAsLazyPagingItems
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.MovieCard
import dev.datlag.mimasu.ui.custom.PersonCard
import dev.datlag.mimasu.ui.custom.ShowCard
import dev.datlag.mimasu.ui.viewmodel.SearchViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search() {
    val searchViewModel = kodeinViewModel<SearchViewModel>()
    val query by searchViewModel.query.collectAsStateWithLifecycle()

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
                            if (query.isNullOrBlank()) {
                                MaterialSymbols(
                                    name = MaterialSymbols.SEARCH,
                                    contentDescription = null
                                )
                            } else {
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
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {

                                }
                            ) {
                                MaterialSymbols(
                                    name = MaterialSymbols.TUNE,
                                    contentDescription = null
                                )
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
        // val results = searchViewModel.multiSearch.collectAsLazyPagingItems()
        val result by searchViewModel.searchResult.collectAsStateWithLifecycle()

        if (result.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (result.error) {
                    // Fail
                    Text("Something went wrong")
                } else {
                    if (query?.trim()?.takeIf { it.length >=  2 }?.isNotBlank() == true) {
                        // Nothing found
                        Text("Nothing found for: $query")
                    } else {
                        // Nothing searched
                        Text("Search for people, show or movies")
                    }
                }
            }
        } else {
            SearchContent(padding, query, result)
        }
    }
}