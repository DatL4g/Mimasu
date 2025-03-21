package dev.datlag.mimasu.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

object Navigation {

    @Serializable
    data object Home

    @Serializable
    data object Detail
}

@Composable
fun Navigation() {
    val controller = rememberNavController()

    NavHost(
        navController = controller,
        startDestination = Navigation.Home
    ) {
        composable<Navigation.Home> {
            Column {
                Text(text = "Home")
                Button(
                    onClick = {
                        controller.navigate(Navigation.Detail)
                    }
                ) {
                    Text(text = "Details")
                }
            }
        }
        composable<Navigation.Detail> {
            Text(text = "Detail Screen")
        }
    }
}