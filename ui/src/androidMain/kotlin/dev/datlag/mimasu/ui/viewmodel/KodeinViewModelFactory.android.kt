package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass
import org.kodein.di.DirectDI

actual fun <T : ViewModel> platformKodeinViewModelFactory(
    di: DirectDI,
    modelClass: KClass<T>,
    extras: CreationExtras
): T? {
    return when {
        else -> null
    }
}