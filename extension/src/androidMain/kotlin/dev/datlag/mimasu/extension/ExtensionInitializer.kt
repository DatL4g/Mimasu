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
            movieProvider = MovieProvider(context)
        ).also { result ->
            state.update { result }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }

    data class State(
        val movieProvider: MovieProvider
    )

    companion object {
        private val state = atomic<State?>(null)

        fun getMovieProvider(context: Context): MovieProvider {
            return state.value?.movieProvider ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(ExtensionInitializer::class.java)
                .movieProvider
        }

        fun nullableMovieProvider(): MovieProvider? = state.value?.movieProvider
    }
}