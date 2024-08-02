package dev.datlag.mimasu.tv

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardBackspace
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SpaceBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.datlag.mimasu.tv.other.KeyGenerator
import kotlinx.serialization.Serializable

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MiniKeyboard(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = { }
) {
    var sizeInDp by remember {
        mutableStateOf(DpSize.Zero)
    }
    val density = LocalDensity.current

    val extrasHeight by remember {
        derivedStateOf {
            sizeInDp.width / 7
        }
    }
    var typed by remember(query) { mutableStateOf(query) }
    var keyboardType by remember {
        mutableStateOf<KeyboardType>(KeyboardType.UpperCase)
    }

    LaunchedEffect(key1 = typed) {
        if (keyboardType !is KeyboardType.NumberAndSpecial) {
            keyboardType = if (typed.isBlank()) {
                KeyboardType.UpperCase
            } else {
                KeyboardType.LowerCase
            }
        }
    }

    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        LazyVerticalGrid(
            modifier = modifier
                .defaultMinSize(minWidth = 250.dp)
                .onSizeChanged {
                    sizeInDp = with(density) {
                        DpSize(
                            width = it.width.toDp(),
                            height = it.height.toDp()
                        )
                    }
                },
            columns = GridCells.Fixed(7)
        ) {
            items(
                items = when (keyboardType) {
                    KeyboardType.LowerCase -> KeyGenerator.alphabetLower + KeyGenerator.specialCharV2
                    KeyboardType.NumberAndSpecial -> KeyGenerator.numbers + KeyGenerator.specialCharV3
                    else -> KeyGenerator.alphabet + KeyGenerator.specialCharV1
                }
            ) {
                KeyItem(
                    key = it
                ) { text ->
                    typed += text
                    onQueryChange(typed)
                }
            }
            item(span = { GridItemSpan(2) }) {
                KeyItem(
                    modifier = Modifier.aspectRatio(2F),
                    onClick = {
                        typed += " "
                        onQueryChange(typed)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SpaceBar,
                        contentDescription = "Space"
                    )
                }
            }
            item(span = { GridItemSpan(2) }) {
                KeyItem(
                    modifier = Modifier.aspectRatio(2f),
                    onClick = {
                        typed = typed.substring(0, typed.length - 1)
                        onQueryChange(typed)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardBackspace,
                        contentDescription = "Backspace",
                    )
                }
            }
            item(span = { GridItemSpan(2) }) {
                KeyItem(
                    modifier = Modifier.aspectRatio(2f),
                    onClick = {
                        onSearch(typed)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                    )
                }
            }
        }
        LazyColumn {
            item {
                KeyItem(
                    modifier = Modifier
                        .width(extrasHeight * 1.5f)
                        .height(extrasHeight),
                    onClick = {
                        typed = ""
                        onQueryChange(typed)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = "Clear",
                    )
                }
            }
            item {
                KeyItem(
                    modifier = Modifier
                        .width(extrasHeight * 1.5f)
                        .height(extrasHeight),
                    onClick = {
                        keyboardType = when (keyboardType) {
                            KeyboardType.NumberAndSpecial -> if (typed.isEmpty()) KeyboardType.UpperCase else KeyboardType.LowerCase
                            else -> KeyboardType.NumberAndSpecial
                        }
                    }
                ) {
                    Text(text = "&123")
                }
            }
            item {
                KeyItem(
                    modifier = Modifier
                        .width(extrasHeight * 1.5f)
                        .height(extrasHeight),
                    enabled = !keyboardType.isNumberAndSpecial,
                    onClick = {
                        keyboardType = when (keyboardType) {
                            is KeyboardType.UpperCase -> KeyboardType.LowerCase
                            is KeyboardType.LowerCase -> KeyboardType.UpperCase
                            else -> keyboardType
                        }
                    }
                ) {
                    Icon(
                        imageVector = when (keyboardType) {
                            is KeyboardType.LowerCase -> Icons.Rounded.KeyboardArrowUp
                            else -> Icons.Rounded.KeyboardArrowDown
                        },
                        contentDescription = "Lower",
                    )
                }
            }
        }
    }
}

@Composable
fun KeyItem(
    key: Any,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    KeyItem(
        modifier = modifier.aspectRatio(1F),
        onClick = { onClick(key.toString()) }
    ) {
        Text(text = key.toString())
    }
}

@Composable
fun KeyItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.padding(4.dp),
        onClick = onClick,
        enabled = enabled,
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.small)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Serializable
private sealed interface KeyboardType {

    val isUpperCase: Boolean
        get() = false

    val isLowerCase: Boolean
        get() = false

    val isNumberAndSpecial: Boolean
        get() = false

    @Serializable
    data object UpperCase : KeyboardType {
        override val isUpperCase: Boolean = true
    }

    @Serializable
    data object LowerCase : KeyboardType {
        override val isLowerCase: Boolean = true
    }

    @Serializable
    data object NumberAndSpecial : KeyboardType {
        override val isNumberAndSpecial: Boolean = true
    }
}