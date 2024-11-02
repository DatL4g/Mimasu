package dev.datlag.mimasu.ui.navigation.screen.movie.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingToolbar(
    state: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    backdrop: String?,
    fallbackBackdrop: String?,
    title: String
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        val imageAlpha by remember(state) {
            derivedStateOf {
                max(min(1F - state.collapsedFraction, 1F), 0F)
            }
        }

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize(),
            model = backdrop,
            contentScale = ContentScale.Crop,
            contentDescription = null,
            error = rememberAsyncImagePainter(
                model = fallbackBackdrop,
                contentScale = ContentScale.Crop
            ),
            alpha = imageAlpha
        )
        LargeTopAppBar(
            navigationIcon = {
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = null
                    )
                }
            },
            title = {
                Text(
                    text = title,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            )
        )
    }
}