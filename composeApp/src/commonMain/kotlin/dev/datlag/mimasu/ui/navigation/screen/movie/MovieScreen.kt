package dev.datlag.mimasu.ui.navigation.screen.movie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.ui.custom.CollapsingToolbar

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
                background = { state ->
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .matchParentSize(),
                        model = component.trending.backdropPicture,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = rememberAsyncImagePainter(
                            model = component.trending.backdropPictureW500,
                            contentScale = ContentScale.Crop
                        ),
                        alpha = state.expandProgress
                    )
                },
                navigationIcon = { state ->
                    IconButton(
                        modifier = if (state.isCollapsed) {
                            Modifier
                        } else {
                            Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5F), CircleShape)
                        },
                        onClick = {
                            component.back()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                },
                title = { state ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                    ) {
                        val subTitle = component.trending.alternativeTitle

                        Text(
                            text = component.trending.title,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = if (state.isExpanded) {
                                LocalTextStyle.current.copy(
                                    shadow = Shadow(
                                        color = MaterialTheme.colorScheme.surface,
                                        offset = Offset(4F, 4F),
                                        blurRadius = 8F
                                    )
                                )
                            } else {
                                LocalTextStyle.current
                            }
                        )
                        AnimatedVisibility(
                            visible = !subTitle.isNullOrBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            if (!subTitle.isNullOrBlank()) {
                                Text(
                                    text = subTitle,
                                    softWrap = true,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    style = if (state.isExpanded) {
                                        MaterialTheme.typography.labelMedium.copy(
                                            shadow = Shadow(
                                                color = MaterialTheme.colorScheme.surface,
                                                offset = Offset(4F, 4F),
                                                blurRadius = 8F
                                            )
                                        )
                                    } else {
                                        MaterialTheme.typography.labelMedium
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) {
        LazyColumn(
            contentPadding = it
        ) {
            item {
                AsyncImage(
                    modifier = Modifier
                        .width(140.dp)
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    model = component.trending.posterPicture,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = rememberAsyncImagePainter(
                        model = component.trending.posterPictureW500,
                        contentScale = ContentScale.Crop
                    )
                )
            }
            item {
                Text(
                    text = "Description"
                )
            }
            item {
                Text(
                    text = component.trending.overview ?: component.trending.alternativeTitle ?: "NaN"
                )
            }
        }
    }
}