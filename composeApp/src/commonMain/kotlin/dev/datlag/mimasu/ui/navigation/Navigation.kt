package dev.datlag.mimasu.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.datlag.mimasu.ui.navigation.home.Home
import kotlinx.serialization.Serializable

object Navigation {

    @Serializable
    data object Home

    @Serializable
    data class Detail(val param: String)
}

@Composable
fun Navigation() {
    val controller = rememberNavController()

    NavigationSuiteScaffold(
        modifier = Modifier.statusBarsPadding(),
        navigationSuiteItems = {
            item(
                selected = controller.currentDestination?.hasRoute<Navigation.Home>() ?: false,
                onClick = {
                    controller.navigate(Navigation.Home)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Home")
                }
            )
        }
    ) {
        NavHost(
            navController = controller,
            startDestination = Navigation.Home
        ) {
            composable<Navigation.Home> {
                Home()
            }
            composable<Navigation.Detail> {
                val route = it.toRoute<Navigation.Detail>()
                Text(text = "Detail Screen${route.param}")
            }
        }
    }
}