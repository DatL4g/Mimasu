package dev.datlag.mimasu.module

import coil3.ImageLoader
import org.kodein.di.DI

actual data object PlatformModule {
    actual val di: DI.Module
        get() = TODO("Not yet implemented")
}

actual fun ImageLoader.Builder.extendImageLoader(): ImageLoader.Builder {
    return this
}