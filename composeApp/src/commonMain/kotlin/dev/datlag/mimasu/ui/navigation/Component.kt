package dev.datlag.mimasu.ui.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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

    val detectCarProjection: Boolean
        get() = true

    val visible: Boolean
        get() = false

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun render(scope: SharedTransitionScope) = DeviceContent(
        common = { renderCommon(scope) },
        tv = { renderTv(scope) },
        car = { renderCar(scope) },
        carDetectProjection = detectCarProjection
    )

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    @NonRestartableComposable
    fun renderCommon(scope: SharedTransitionScope)

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    @NonRestartableComposable
    fun renderTv(scope: SharedTransitionScope) = renderCommon(scope)

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun renderCar(scope: SharedTransitionScope) = renderCommon(scope)

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