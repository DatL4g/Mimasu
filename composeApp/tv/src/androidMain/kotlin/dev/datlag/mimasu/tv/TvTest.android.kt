package dev.datlag.mimasu.tv

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.material3.Button
import androidx.tv.material3.DrawerState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowScope
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import dev.datlag.mimasu.tv.common.not
import dev.datlag.mimasu.tv.common.rememberDirectionAwareKeyboardAlignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
actual fun TvTest() {
    val state = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column {
        TabRow(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            selectedTabIndex = selectedTabIndex,
            indicator = { _, _ ->
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(Color.Transparent)
                )
            }
        ) {
            NavTab(
                icon = Icons.Rounded.AccountCircle,
                label = "Profile",
                isSelected = selectedTabIndex == -1,
                onSelect = { selectedTabIndex = -1 },
                circle = true
            )
            NavTab(
                icon = Icons.Rounded.Home,
                label = "Home",
                isSelected = selectedTabIndex == 0,
                onSelect = { selectedTabIndex = 0 }
            )
            NavTab(
                icon = Icons.Rounded.Favorite,
                label = "Favorite",
                isSelected = selectedTabIndex == 1,
                onSelect = { selectedTabIndex = 1 }
            )
            NavTab(
                icon = Icons.Rounded.Search,
                label = "Search",
                isSelected = selectedTabIndex == 2,
                onSelect = { selectedTabIndex = 2 }
            )
        }
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = selectedTabIndex == 2
            ) {
                Column {
                    var query by remember { mutableStateOf("") }

                    SearchView(query)
                    MiniKeyboard(
                        query = query,
                        onQueryChange = {
                            query = it.trimStart()
                        },
                        modifier = Modifier.width(250.dp)
                    )
                }
            }
            Content(state = state)
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

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun Content(state: DrawerState) {
    val alignment = rememberDirectionAwareKeyboardAlignment()
    val data by counter.collectAsStateWithLifecycle(initialValue = 0)
    Column {
        Text(text = "Direction Keyboard: ${alignment.option}")
        Text(text = "Direction Keyboard: ${(!alignment).option}")
        Text(text = "Counter: $data")
        Button(
            onClick = {
                state.setValue(!state.currentValue)
            }
        ) {
            Text(text = "Toggle")
        }
    }
}

val counter = flow {
    var count = 0
    while (count < 5) {
        emit(++count)
        delay(1000)
    }
}