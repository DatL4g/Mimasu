package dev.datlag.mimasu.tv.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import dev.datlag.mimasu.core.serialization.SerializableImmutableList
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable

internal object Keyboard {
    private val alphabet: SerializableImmutableList<Char> = ('A'..'Z').toImmutableList()
    private val specialCharV1: SerializableImmutableList<Char> = persistentSetOf('-', '\'').toImmutableList()
    private val specialCharV2: SerializableImmutableList<Char> = persistentSetOf('_', ',').toImmutableList()
    private val specialCharV3: SerializableImmutableList<Char> = persistentSetOf('&', '?', '!', '%', ':', '.', ';', '#', '+').toImmutableList()
    private val alphabetLower: SerializableImmutableList<Char> = ('a'..'z').toImmutableList()
    private val numbers: SerializableImmutableList<Char> = ('0'..'9').toImmutableList()

    private const val TYPE_TEXT = "&123"

    @OptIn(MainThread::class)
    @Composable
    operator fun invoke(
        value: String,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier
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

        var typed by remember(value) { mutableStateOf(value) }
        var keyboardType by remember {
            mutableStateOf<Type>(Type.UpperCase)
        }

        LaunchedMain(typed) {
            if (keyboardType !is Type.NumberAndSpecial) {
                keyboardType = if (typed.isBlank()) {
                    Type.UpperCase
                } else {
                    Type.LowerCase
                }
            }
        }

        Row(
            modifier = Modifier.onKeyEvent { event ->
                val upperCase = event.isShiftPressed
                if (event.type != KeyEventType.KeyDown) {
                    return@onKeyEvent false
                }

                val char = event.key.char
                if (char != null) {
                    typed += if (upperCase) {
                        char.uppercase()
                    } else {
                        char.lowercase()
                    }
                    onValueChange(typed)
                    return@onKeyEvent true
                }

                when (event.key) {
                    Key.Backspace -> {
                        typed = typed.dropLast(1)
                        onValueChange(typed)
                        true
                    }
                    Key.Clear -> {
                        typed = ""
                        onValueChange(typed)
                        true
                    }
                    else -> false
                }
            }
        ) {
            LazyVerticalGrid(
                modifier = modifier
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
                        is Type.LowerCase -> alphabetLower + specialCharV2
                        is Type.NumberAndSpecial -> numbers + specialCharV3
                        else -> alphabet + specialCharV1
                    }
                ) { char ->
                    KeyItem(
                        key = char.toString(),
                        onClick = {
                            typed += char
                            onValueChange(typed)
                        }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    KeyItem(
                        onClick = {
                            typed += ' '
                            onValueChange(typed)
                        },
                        modifier = Modifier.aspectRatio(2F),
                        content = {
                            MaterialSymbols(
                                name = MaterialSymbols.SPACE_BAR,
                                contentDescription = null
                            )
                        }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    KeyItem(
                        onClick = {
                            typed = typed.dropLast(1)
                            onValueChange(typed)
                        },
                        modifier = Modifier.aspectRatio(2F),
                        content = {
                            MaterialSymbols(
                                name = MaterialSymbols.KEYBOARD_BACKSPACE,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            LazyColumn {
                item {
                    KeyItem(
                        modifier = Modifier
                            .width(extrasHeight * 1.5F)
                            .height(extrasHeight),
                        onClick = {
                            typed = ""
                            onValueChange(typed)
                        },
                        content = {
                            MaterialSymbols(
                                name = MaterialSymbols.CANCEL,
                                contentDescription = null
                            )
                        }
                    )
                }
                item {
                    KeyItem(
                        modifier = Modifier
                            .width(extrasHeight * 1.5F)
                            .height(extrasHeight),
                        onClick = {
                            keyboardType = when (keyboardType) {
                                is Type.NumberAndSpecial -> if (typed.isEmpty()) {
                                    Type.UpperCase
                                } else {
                                    Type.LowerCase
                                }
                                else -> Type.NumberAndSpecial
                            }
                        },
                        content = {
                            Text(text = TYPE_TEXT)
                        }
                    )
                }
                item {
                    KeyItem(
                        modifier = Modifier
                            .width(extrasHeight * 1.5F)
                            .height(extrasHeight),
                        onClick = {
                            keyboardType = when (keyboardType) {
                                is Type.UpperCase -> Type.LowerCase
                                is Type.LowerCase -> Type.UpperCase
                                else -> keyboardType
                            }
                        },
                        enabled = keyboardType !is Type.NumberAndSpecial,
                        content = {
                            MaterialSymbols(
                                name = when (keyboardType) {
                                    is Type.LowerCase -> MaterialSymbols.KEYBOARD_ARROW_UP
                                    else -> MaterialSymbols.KEYBOARD_ARROW_DOWN
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun KeyItem(
        key: String,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        KeyItem(
            modifier = modifier.aspectRatio(1F),
            onClick = onClick
        ) {
            Text(text = key)
        }
    }

    @Composable
    private fun KeyItem(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        content: @Composable BoxScope.() -> Unit
    ) {
        Surface(
            modifier = modifier.padding(4.dp),
            onClick = onClick,
            enabled = enabled,
            shape = ClickableSurfaceDefaults.shape(
                shape = MaterialTheme.shapes.small
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = content
            )
        }
    }

    @Serializable
    sealed interface Type {

        @Serializable
        data object UpperCase : Type

        @Serializable
        data object LowerCase : Type

        @Serializable
        data object NumberAndSpecial : Type
    }

    private val Key.char: Char?
        get() = when (this) {
            Key.A -> 'a'
            Key.B -> 'b'
            Key.C -> 'c'
            Key.D -> 'd'
            Key.E -> 'e'
            Key.F -> 'f'
            Key.G -> 'g'
            Key.H -> 'h'
            Key.I -> 'i'
            Key.J -> 'j'
            Key.K -> 'k'
            Key.L -> 'l'
            Key.M -> 'm'
            Key.N -> 'n'
            Key.O -> 'o'
            Key.P -> 'p'
            Key.Q -> 'q'
            Key.R -> 'r'
            Key.S -> 's'
            Key.T -> 't'
            Key.U -> 'u'
            Key.V -> 'v'
            Key.W -> 'w'
            Key.X -> 'x'
            Key.Y -> 'y'
            Key.Z -> 'z'
            Key.Zero -> '0'
            Key.One -> '1'
            Key.Two -> '2'
            Key.Three -> '3'
            Key.Four -> '4'
            Key.Five -> '5'
            Key.Six -> '6'
            Key.Seven -> '7'
            Key.Eight -> '8'
            Key.Nine -> '9'
            Key.Spacebar -> ' '
            Key.Minus, Key.NumPadSubtract -> '-'
            Key.Apostrophe -> '\''
            Key.Comma, Key.NumPadComma -> ','
            Key.Period, Key.NumPadDot -> '.'
            Key.Semicolon -> ';'
            Key.Pound -> '#'
            Key.Plus -> '+'
            else -> null
        }
}