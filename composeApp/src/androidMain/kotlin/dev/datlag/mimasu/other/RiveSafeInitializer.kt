package dev.datlag.mimasu.other

import android.content.Context
import androidx.annotation.Keep
import androidx.startup.Initializer
import app.rive.runtime.kotlin.core.Rive
import dev.datlag.mimasu.common.initSafely

@Keep
class RiveSafeInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Rive.initSafely(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}