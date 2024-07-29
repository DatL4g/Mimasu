package dev.datlag.mimasu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.SideEffect
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import dev.datlag.mimasu.ui.custom.DeviceContent
import dev.datlag.tooling.compose.launchDefault
import dev.datlag.tooling.compose.launchIO
import dev.datlag.tooling.compose.launchMain
import dev.datlag.tooling.decompose.defaultScope
import dev.datlag.tooling.decompose.ioScope
import dev.datlag.tooling.decompose.lifecycle.LocalLifecycleOwner
import dev.datlag.tooling.decompose.mainScope
import kotlinx.coroutines.CoroutineScope
import org.kodein.di.DIAware
import org.kodein.di.compose.withDI

interface Component : DIAware, ComponentContext {

    @Composable
    fun render() = DeviceContent(
        common = { renderCommon() },
        tv = { renderTv() }
    )

    @Composable
    @NonRestartableComposable
    fun renderCommon()

    @Composable
    @NonRestartableComposable
    fun renderTv() = renderCommon()

    fun launchIO(block: suspend CoroutineScope.() -> Unit) = ioScope().launchIO(block)
    fun launchMain(block: suspend CoroutineScope.() -> Unit) = mainScope().launchMain(block)
    fun launchDefault(block: suspend CoroutineScope.() -> Unit) = defaultScope().launchDefault(block)

    @Composable
    fun onRender(content: @Composable () -> Unit) = withDI(di) {
        CompositionLocalProvider(
            LocalLifecycleOwner provides object : LifecycleOwner {
                override val lifecycle: Lifecycle = this@Component.lifecycle
            }
        ) {
            content()
        }
        SideEffect {
            // ToDo("crashlytics screen log")
        }
    }
}