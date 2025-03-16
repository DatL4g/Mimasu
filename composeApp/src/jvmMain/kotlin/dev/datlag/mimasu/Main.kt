package dev.datlag.mimasu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.tooling.Tooling
import dev.datlag.tooling.applicationTitle
import org.kodein.di.DI

fun main(vararg args: String) {
    val di = DI {

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
            App(di) {

            }
        }
    }
}