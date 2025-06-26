package dev.datlag.mimasu.extension

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Keep
class ExtensionInitializer : Initializer<ExtensionInitializer.State> {

    override fun create(context: Context): State {
        return State(
            updateProvider = UpdateProviderAndroid(context),
            showProviderAndroid = ShowProviderAndroid(context).also {
                GlobalScope.launch {
                    it.initialize()
                }
            }
        ).also { result ->
            state.update { result }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }

    data class State(
        val updateProvider: UpdateProviderAndroid,
        val showProviderAndroid: ShowProviderAndroid
    )

    companion object {
        private val state = atomic<State?>(null)

        fun getUpdateProvider(context: Context): UpdateProviderAndroid {
            return state.value?.updateProvider ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(ExtensionInitializer::class.java)
                .updateProvider
        }

        fun getShowProvider(context: Context): ShowProviderAndroid {
            return state.value?.showProviderAndroid ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(ExtensionInitializer::class.java)
                .showProviderAndroid
        }

        private fun nullableUpdateProvider(): UpdateProviderAndroid? = state.value?.updateProvider

        private fun nullableShowProvider(): ShowProviderAndroid? = state.value?.showProviderAndroid

        fun unbindAll(context: Context) {
            nullableUpdateProvider()?.unbind(context)
            nullableShowProvider()?.unbind(context)
        }

        suspend fun initialize(context: Context) {
            getShowProvider(context).initialize()
        }

        suspend fun rebindAll(context: Context) {
            nullableUpdateProvider()?.rebind(context)
            nullableShowProvider()?.rebind(context)
        }

        suspend fun rebindIfNoneAvailable(context: Context) {
            nullableUpdateProvider()?.rebindIfUnavailable(context)
            nullableShowProvider()?.rebindIfNoneAvailable(context)
        }

        fun rebindIfNoneAvailable(scope: CoroutineScope, context: Context) = scope.launch {
            rebindIfNoneAvailable(context)
        }
    }
}