package dev.datlag.mimasu.ui

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import app.rive.runtime.kotlin.core.Rive
import dev.datlag.mimasu.ui.common.initSafely
import dev.datlag.sekret.NativeLoader
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.serialization.Serializable

@Keep
class AppInitializer : Initializer<AppInitializer.State> {

    override fun create(context: Context): State {
        return State(
            sekretLoaded = NativeLoader.loadLibrary(context, SEKRET_LIB),
            riveLoaded = Rive.initSafely(context)
        ).also { result ->
            state.update { result }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }

    @Serializable
    data class State(
        val sekretLoaded: Boolean,
        val riveLoaded: Boolean
    )

    companion object {
        private val state = atomic<State?>(null)
        private const val SEKRET_LIB = "sekret"

        fun isSekretLoaded(context: Context): Boolean {
            return state.value?.sekretLoaded?.takeIf { it } ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(AppInitializer::class.java)
                .sekretLoaded.takeIf { it }
            ?: NativeLoader.loadLibrary(context, SEKRET_LIB)
        }

        fun isRiveLoaded(context: Context): Boolean {
            return state.value?.riveLoaded?.takeIf { it } ?: androidx.startup.AppInitializer
                .getInstance(context)
                .initializeComponent(AppInitializer::class.java)
                .riveLoaded.takeIf { it }
            ?: Rive.initSafely(context)
        }
    }
}