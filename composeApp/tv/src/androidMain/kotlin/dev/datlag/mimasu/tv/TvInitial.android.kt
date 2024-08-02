package dev.datlag.mimasu.tv

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowScope
import androidx.tv.material3.Text

@Composable
actual fun TvInitial(
    profile: TvInitialNavigation,
    home: TvInitialNavigation,
    favorites: TvInitialNavigation,
    search: TvInitialSearch,
    content: @Composable () -> Unit
) {
    Column {
        var showKeyboard by remember {
            mutableStateOf(false)
        }

        TabRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            selectedTabIndex = when {
                profile.selected -> 0
                home.selected -> 1
                favorites.selected -> 2
                else -> 1
            },
            indicator = { _, _ ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Transparent)
                )
            }
        ) {
            NavTab(
                icon = profile.icon,
                label = profile.label,
                isSelected = profile.selected,
                onSelect = profile.onClick
            )
            NavTab(
                icon = home.icon,
                label = home.label,
                isSelected = home.selected,
                onSelect = home.onClick
            )
            NavTab(
                icon = favorites.icon,
                label = favorites.label,
                isSelected = favorites.selected,
                onSelect = favorites.onClick
            )
            NavTab(
                icon = Icons.Rounded.Search,
                label = "Search",
                isSelected = showKeyboard,
                onSelect = { showKeyboard = !showKeyboard }
            )
        }

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(visible = showKeyboard) {
                Column {
                    SearchView(search.query)
                    MiniKeyboard(
                        query = search.query,
                        onQueryChange = {
                            search.onQueryChange(it.trimStart())
                        },
                        onSearch = search.onSearch,
                        modifier = Modifier.width(250.dp)
                    )
                }
            }
            content()
        }
    }
}

@Composable
fun TabRowScope.NavTab(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    enabled: Boolean = true,
    circle: Boolean = false,
    onSelect: () -> Unit
) {
    val interaction = remember {
        MutableInteractionSource()
    }
    val isFocused by interaction.collectIsFocusedAsState()

    Tab(
        selected = isSelected,
        enabled = enabled,
        onFocus = { },
        onClick = onSelect,
        interactionSource = interaction,
        colors = TabDefaults.pillIndicatorTabColors(
            selectedContentColor = MaterialTheme.colorScheme.onSurface,
            focusedContentColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                when {
                    isFocused -> MaterialTheme.colorScheme.inverseSurface
                    isSelected -> MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = 0.5f
                    )

                    else -> Color.Transparent
                }, if (circle) CircleShape else MaterialTheme.shapes.extraLarge
            )
            .padding(8.dp)
    ) {
        if (circle) {
            Icon(
                modifier = Modifier,
                imageVector = icon,
                contentDescription = label
            )
        } else {
            Icon(
                modifier = Modifier.padding(horizontal = 4.dp),
                imageVector = icon,
                contentDescription = label
            )
            Text(text = label)
        }
    }
}