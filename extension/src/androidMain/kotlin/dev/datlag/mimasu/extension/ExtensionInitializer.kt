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
            },
            movieProviderAndroid = MovieProviderAndroid(context).also {
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
        val showProviderAndroid: ShowProviderAndroid,
        val movieProviderAndroid: MovieProviderAndroid
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

        fun getMovieProvider(context: Context): MovieProviderAndroid {
            return state.value?.movieProviderAndroid ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(ExtensionInitializer::class.java)
                .movieProviderAndroid
        }

        private fun nullableUpdateProvider(): UpdateProviderAndroid? = state.value?.updateProvider

        private fun nullableShowProvider(): ShowProviderAndroid? = state.value?.showProviderAndroid

        private fun nullableMovieProvider(): MovieProviderAndroid? = state.value?.movieProviderAndroid

        fun unbindAll(context: Context) {
            nullableUpdateProvider()?.unbind(context)
            nullableShowProvider()?.unbind(context)
            nullableMovieProvider()?.unbind(context)
        }

        suspend fun initialize(context: Context) {
            getShowProvider(context).initialize()
            getMovieProvider(context).initialize()
        }

        suspend fun rebindAll(context: Context) {
            nullableUpdateProvider()?.rebind(context)
            nullableShowProvider()?.rebind(context)
            nullableMovieProvider()?.rebind(context)
        }

        suspend fun rebindIfNoneAvailable(context: Context) {
            nullableUpdateProvider()?.rebindIfUnavailable(context)
            nullableShowProvider()?.rebindIfNoneAvailable(context)
            nullableMovieProvider()?.rebindIfNoneAvailable(context)
        }

        fun rebindIfNoneAvailable(scope: CoroutineScope, context: Context) = scope.launch {
            rebindIfNoneAvailable(context)
        }
    }
}