package dev.datlag.mimasu.tv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.datlag.mimasu.tv.ui.navigation.home.Home
import kotlinx.serialization.Serializable

object Navigation {

    @Serializable
    data object Home
}

@Composable
fun Navigation() {
    val controller = rememberNavController()

    NavHost(
        navController = controller,
        startDestination = Navigation.Home
    ) {
        composable<Navigation.Home> {
            Home()
        }
    }
}