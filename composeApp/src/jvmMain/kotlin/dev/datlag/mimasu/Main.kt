package dev.datlag.mimasu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureIcon
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.ui.navigation.RootComponent
import dev.datlag.sekret.NativeLoader
import dev.datlag.tooling.Platform
import dev.datlag.tooling.Tooling
import dev.datlag.tooling.applicationTitle
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.scopeCatching
import dev.datlag.tooling.systemProperty
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File
import javax.swing.SwingUtilities

fun main(vararg args: String) {
    Napier.base(DebugAntilog())
    NativeLoader.loadLibrary(
        name = "sekret",
        path = systemProperty("compose.application.resources.dir")?.let(::File)
    ).also {
        Napier.e("Loaded sekrets: $it")
    }

    val di = DI {
        import(NetworkModule.di)
    }

    runWindow(di)
}

@OptIn(DelicateCoilApi::class)
private fun runWindow(di: DI) {
    Tooling.applicationTitle("Mimasu")

    val imageLoader by di.instance<ImageLoader>()
    SingletonImageLoader.setUnsafe(imageLoader)

    val windowState = WindowState()
    val lifecycle = LifecycleRegistry()
    val lifecycleOwner = object : LifecycleOwner {
        override val lifecycle: Lifecycle = lifecycle
    }
    val backDispatcher = BackDispatcher()
    val root = runOnUiThread {
        RootComponent(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycle,
                backHandler = backDispatcher
            ),
            di = di
        )
    }

    application(exitProcessOnExit = true) {
        Window(
            state = windowState,
            title = "Mimasu",
            onCloseRequest = ::exitApplication
        ) {
            LifecycleController(lifecycle, windowState)

            CompositionLocalProvider(
                LocalLifecycleOwner provides lifecycleOwner
            ) {
                App(di) {
                    PredictiveBackGestureOverlay(
                        backDispatcher = backDispatcher,
                        backIcon = { progress, _ ->
                            PredictiveBackGestureIcon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                progress = progress,
                                iconTintColor = Platform.colorScheme().onSecondaryContainer,
                                backgroundColor = Platform.colorScheme().secondaryContainer
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        root.render()
                    }
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }

    var error: Throwable? = null
    var result: T? = null

    SwingUtilities.invokeAndWait {
        val res = scopeCatching(block)
        error = res.exceptionOrNull()
        result = res.getOrNull()
    }

    error?.also { throw it }

    return result as T
}