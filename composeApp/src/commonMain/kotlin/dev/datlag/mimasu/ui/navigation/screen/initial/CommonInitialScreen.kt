package dev.datlag.mimasu.ui.navigation.screen.initial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.datlag.mimasu.ui.custom.AdaptiveScaffold

@Composable
fun CommonInitialScreen(
    content: @Composable () -> Unit
) {
    AdaptiveScaffold(
        navigationSuiteItems = {
            item(
                selected = false,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Profile")
                },
                onClick = { }
            )
            item(
                selected = true,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Home")
                },
                onClick = { }
            )
            item(
                selected = false,
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Favorites")
                },
                onClick = { }
            )
        }
    ) {
        content()
    }
}