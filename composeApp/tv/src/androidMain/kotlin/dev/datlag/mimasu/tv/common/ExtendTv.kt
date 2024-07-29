package dev.datlag.mimasu.tv.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.text.TvKeyboardAlignment
import androidx.tv.material3.DrawerValue

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun rememberDirectionAwareKeyboardAlignment(): TvKeyboardAlignment {
    val direction = LocalLayoutDirection.current
    return remember(direction) {
        when (direction) {
            LayoutDirection.Ltr -> TvKeyboardAlignment.Right
            LayoutDirection.Rtl -> TvKeyboardAlignment.Left
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
operator fun TvKeyboardAlignment.not(): TvKeyboardAlignment = when (this) {
    TvKeyboardAlignment.Left -> TvKeyboardAlignment.Right
    TvKeyboardAlignment.Right -> TvKeyboardAlignment.Left
    else -> this
}

operator fun DrawerValue.not(): DrawerValue = when (this) {
    DrawerValue.Open -> DrawerValue.Closed
    DrawerValue.Closed -> DrawerValue.Open
}