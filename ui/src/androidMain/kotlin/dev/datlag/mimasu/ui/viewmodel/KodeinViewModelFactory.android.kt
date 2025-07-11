package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import org.kodein.di.DirectDI
import kotlin.reflect.KClass

actual fun <T : ViewModel> platformKodeinViewModelFactory(
    di: DirectDI,
    modelClass: KClass<T>,
    extras: CreationExtras
): T? {
    return when {
        else -> null
    }
}