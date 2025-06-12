package dev.datlag.mimasu.module

import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.UpdateProvider
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object ExtensionModule {

    private const val NAME = "AndroidExtensionModule"

    val di: DI.Module = DI.Module(NAME) {
        bindSingleton<UpdateProvider> {
            ExtensionInitializer.getUpdateProvider(context = instance())
        }
    }
}