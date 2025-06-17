package dev.datlag.mimasu.extension

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

@Keep
class ExtensionInitializer : Initializer<ExtensionInitializer.State> {

    override fun create(context: Context): State {
        return State(
            updateProvider = UpdateProviderAndroid(context),
            showProviderAndroid = ShowProviderAndroid(context)
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

        fun rebindAll(context: Context) {
            nullableUpdateProvider()?.rebind(context)
            nullableShowProvider()?.rebind(context)
        }

        fun rebindIfNoneAvailable(context: Context) {
            nullableUpdateProvider()?.rebindIfUnavailable(context)
            nullableShowProvider()?.rebindIfNoneAvailable(context)
        }
    }
}