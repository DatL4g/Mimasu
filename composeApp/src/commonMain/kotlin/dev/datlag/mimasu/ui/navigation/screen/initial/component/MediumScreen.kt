package dev.datlag.mimasu.ui.navigation.screen.initial.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MediumScreen(content: @Composable () -> Unit) {
    Row {
        NavigationRail {
            Spacer(modifier = Modifier.weight(1F))
            NavigationRailItem(
                selected = false,
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Profile")
                }
            )
            NavigationRailItem(
                selected = true,
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Home")
                }
            )
            NavigationRailItem(
                selected = false,
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Favorites")
                }
            )
            Spacer(modifier = Modifier.weight(1F))
        }
        content()
    }
}