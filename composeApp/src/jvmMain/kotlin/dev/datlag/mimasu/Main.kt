package dev.datlag.mimasu

import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.ui.navigation.Navigation
import dev.datlag.tooling.Tooling
import dev.datlag.tooling.applicationTitle
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.kodein.di.DI

fun main(vararg args: String) {
    val di = DI {
        import(NetworkModule.di)
    }

    runWindow(di)
}

private fun runWindow(di: DI) {
    Tooling.applicationTitle("Mimasu")

    application(exitProcessOnExit = true) {
        Window(
            title = "Mimasu",
            onCloseRequest = ::exitApplication
        ) {
            DevelopmentEntryPoint {
                App(di) {
                    Navigation()
                }
            }
        }
    }
}