package dev.datlag.mimasu.ui.navigation.screen.movie

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dev.datlag.mimasu.ui.navigation.screen.movie.component.CollapsingToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(component: MovieComponent) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingToolbar(
                state = appBarState,
                scrollBehavior = scrollBehavior,
                backdrop = component.trending.backdropPicture,
                fallbackBackdrop = component.trending.backdropPictureW500,
                title = component.trending.title
            )
        }
    ) {
        Text(
            modifier = Modifier.padding(it),
            text = component.trending.overview ?: component.trending.alternativeTitle ?: "NaN"
        )
    }
}