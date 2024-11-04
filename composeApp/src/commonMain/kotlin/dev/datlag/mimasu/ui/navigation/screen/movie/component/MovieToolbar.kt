package dev.datlag.mimasu.ui.navigation.screen.movie.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.ui.custom.CollapsingToolbar
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieToolbar(
    appBarState: TopAppBarState,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String,
    subTitle: String?,
    backdrop: String?,
    fallbackBackdrop: String?,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    CollapsingToolbar(
        state = appBarState,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        background = { state ->
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .matchParentSize(),
                model = backdrop,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = rememberAsyncImagePainter(
                    model = fallbackBackdrop,
                    contentScale = ContentScale.Crop
                ),
                error = rememberAsyncImagePainter(
                    model = fallbackBackdrop,
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
                    Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = state.expandProgress * 0.5F), CircleShape)
                },
                onClick = onBack
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
                Text(
                    text = title,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = if (!state.isCollapsed) {
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
                if (!subTitle.isNullOrBlank()) {
                    Text(
                        text = subTitle,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = if (!state.isCollapsed) {
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
    )
}