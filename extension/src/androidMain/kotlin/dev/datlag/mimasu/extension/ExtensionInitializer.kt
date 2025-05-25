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
            movieProvider = MovieProvider(context)
        ).also { result ->
            state.update { result }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }

    data class State(
        val updateProvider: UpdateProviderAndroid,
        val movieProvider: MovieProvider
    )

    companion object {
        private val state = atomic<State?>(null)

        fun getUpdateProvider(context: Context): UpdateProvider {
            return state.value?.updateProvider ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(ExtensionInitializer::class.java)
                .updateProvider
        }

        fun getMovieProvider(context: Context): MovieProvider {
            return state.value?.movieProvider ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(ExtensionInitializer::class.java)
                .movieProvider
        }

        fun nullableMovieProvider(): MovieProvider? = state.value?.movieProvider

        fun nullableUpdateProvider(): UpdateProviderAndroid? = state.value?.updateProvider

        fun unbindAll(context: Context) {
            nullableMovieProvider()?.unbind(context)
            nullableUpdateProvider()?.unbind(context)
        }

        fun rebindAll(context: Context) {
            nullableMovieProvider()?.rebind(context)
            nullableUpdateProvider()?.rebind(context)
        }
    }
}