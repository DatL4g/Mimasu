package dev.datlag.mimasu.tv

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.material3.Button
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
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

    NavigationDrawer(
        drawerState = state,
        drawerContent = {
            var selected by remember {
                mutableStateOf(false)
            }

            NavigationDrawerItem(
                selected = selected,
                onClick = { selected = !selected },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = null
                    )
                },
                content = {
                    Text(text = "Home")
                }
            )
        }
    ) {
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
}

val counter = flow {
    var count = 0
    while (count < 5) {
        emit(++count)
        delay(1000)
    }
}