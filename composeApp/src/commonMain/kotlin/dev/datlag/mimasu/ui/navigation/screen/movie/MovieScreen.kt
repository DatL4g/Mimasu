package dev.datlag.mimasu.ui.navigation.screen.movie

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(component: MovieComponent) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = component.trending.title
                    )
                }
            )
        }
    ) {
        Text(
            modifier = Modifier.padding(it),
            text = component.trending.overview ?: component.trending.alternativeTitle ?: "NaN"
        )
    }
}