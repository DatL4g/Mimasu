package dev.datlag.mimasu.ui.navigation.screen.video.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.PublicOff
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.other.PiPHelper
import dev.datlag.mimasu.ui.navigation.screen.video.VideoPlayerState
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import mimasu.composeapp.generated.resources.Res
import mimasu.composeapp.generated.resources.allStringResources
import mimasu.composeapp.generated.resources.no_subtitle
import mimasu.composeapp.generated.resources.us
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun TopControls(
    state: VideoPlayerState,
    pipActive: Boolean = PiPHelper.active.value
) {
    val visibility by state.controlsVisibility.collectAsStateWithLifecycle()

    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
        visible = visibility && !pipActive,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        val subTitle by state.subTitle.collectAsStateWithLifecycle()

        TopAppBar(
            title = {
                Text(text = "Video Player")
            },
            actions = {
                var expanded by remember { mutableStateOf(false) }

                if (subTitle.available.isNotEmpty() || subTitle.selected != null) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        IconButton(
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            onClick = {
                                state.showControls(2.minutes)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Subtitles,
                                contentDescription = null
                            )
                        }
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                                state.hideControls()
                            },
                            matchTextFieldWidth = false
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    state.select(null)

                                    expanded = false
                                    state.hideControls()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.PublicOff,
                                        contentDescription = null
                                    )
                                },
                                text = {
                                    Text(text = stringResource(Res.string.no_subtitle))
                                },
                                trailingIcon = if (subTitle.selected == null) {
                                    {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null
                                        )
                                    }
                                } else null,
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                            subTitle.available.forEach { lang ->
                                DropdownMenuItem(
                                    onClick = {
                                        state.select(lang)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            modifier = Modifier.size(24.dp).clip(CircleShape),
                                            painter = lang.icon?.let {
                                                painterResource(it)
                                            } ?: rememberVectorPainter(Icons.Rounded.Public),
                                            contentDescription = null,
                                            tint = if (lang.icon != null) {
                                                Color.Unspecified
                                            } else {
                                                LocalContentColor.current
                                            }
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = lang.name,
                                            maxLines = 1,
                                            softWrap = true,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    trailingIcon = if (subTitle.selected == lang) {
                                        {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null
                                            )
                                        }
                                    } else null,
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = Color.White,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )
    }
}