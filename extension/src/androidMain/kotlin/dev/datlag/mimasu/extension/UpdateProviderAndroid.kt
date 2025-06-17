package dev.datlag.mimasu.extension

import android.content.Context
import dev.datlag.mimasu.extension.model.Update
import dev.datlag.mimasu.extension.service.UpdateService
import kotlinx.coroutines.flow.StateFlow

class UpdateProviderAndroid(context: Context) : UpdateProvider {

    private var service = UpdateService(context).also { service ->
        AIDLService.bind(context, service, AIDLService.EXTENSION_PACKAGE)
    }

    override val update: StateFlow<Update?> = service.update

    fun unbind(context: Context): Boolean {
        return AIDLService.unbind(context, service)
    }

    fun rebind(context: Context) {
        unbind(context)

        service = UpdateService(context).also { service ->
            AIDLService.bind(context, service, AIDLService.EXTENSION_PACKAGE)
        }
    }

    fun rebindIfUnavailable(context: Context) {
        if (!service.isBound) {
            rebind(context)
        }
    }
}