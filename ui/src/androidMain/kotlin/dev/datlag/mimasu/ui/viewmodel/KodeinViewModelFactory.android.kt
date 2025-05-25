package dev.datlag.mimasu.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass
import dev.datlag.mimasu.core.typeOf
import io.ktor.client.HttpClient
import org.kodein.di.DirectDI
import org.kodein.di.instance
import org.kodein.di.instanceOrNull
import ru.solrudev.ackpine.installer.PackageInstaller

actual fun <T : ViewModel> platformKodeinViewModelFactory(
    di: DirectDI,
    modelClass: KClass<T>,
    extras: CreationExtras
): T? {
    return when {
        modelClass typeOf ExtensionUpdateViewModel::class -> {
            val context = di.instance<Context>()
            val httpClient = di.instance<HttpClient>()
            val packageInstaller = di.instanceOrNull<PackageInstaller>() ?: PackageInstaller.getInstance(context)

            val model = ExtensionUpdateViewModel(
                filesDir = context.filesDir,
                httpClient = httpClient,
                packageInstaller = packageInstaller
            )

            model as? T
        }
        else -> null
    }
}