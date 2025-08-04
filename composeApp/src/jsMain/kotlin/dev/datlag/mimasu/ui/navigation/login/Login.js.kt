package dev.datlag.mimasu.ui.navigation.login

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols

@Composable
actual fun rememberAppImage(): Painter {
    return MaterialSymbols.rememberPainter(
        name = MaterialSymbols.PERSON,
    ) ?: rememberVectorPainter(Icons.Rounded.Person)
}