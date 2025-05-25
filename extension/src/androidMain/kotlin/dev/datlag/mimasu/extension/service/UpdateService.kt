package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IUpdateProvider
import dev.datlag.mimasu.extension.model.Update
import dev.datlag.mimasu.extension.update.Callback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UpdateService(context: Context) : AIDLService<IUpdateProvider>(context) {
    override val connectionAction: String = ACTION

    private val _update = MutableStateFlow<Update?>(null)
    val update = _update.asStateFlow()

    override fun bind(service: IBinder?): IUpdateProvider? {
        return IUpdateProvider.Stub.asInterface(service)
    }

    override fun onConnected(service: IUpdateProvider) {
        service.requestUpdate(object : Callback.Stub() {
            override fun onResult(info: ByteArray?) {
                _update.update { Update(info) }
            }
        })
    }

    override fun onDisconnected() { }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.IUpdateProvider"
    }
}