package dev.datlag.mimasu.core

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import dev.datlag.mimasu.core.model.UpdateInfo
import dev.datlag.mimasu.core.update.IUpdateCheckCallback
import dev.datlag.mimasu.core.update.IUpdateInfo
import dev.datlag.mimasu.core.update.IUpdateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object MimasuConnection : ServiceConnection {
    const val CONNECTION_ACTION = "dev.datlag.mimasu.core.IMimasuService"
    private var boundService: IMimasuService? = null

    private val _bound = MutableStateFlow(false)
    val bound: StateFlow<Boolean> = _bound.asStateFlow()

    private val _isVideoPlayerSecure = MutableStateFlow(boundService?.isVideoPlayerSecure ?: true)
    val isVideoPlayerSecure: StateFlow<Boolean> = _isVideoPlayerSecure.asStateFlow()

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.boundService = IMimasuService.Stub.asInterface(service)

        _bound.update { true }
        _isVideoPlayerSecure.update { boundService?.isVideoPlayerSecure ?: true }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        this.boundService = null

        _bound.update { false }
        _isVideoPlayerSecure.update { true }
    }

    object Update : AIDLService<IUpdateService>(), UpdateInfo {
        override val connectionAction: String = "dev.datlag.mimasu.core.update.IUpdateService"

        private val _available = MutableStateFlow(false)
        override val available = _available.asStateFlow()

        private val _required = MutableStateFlow(false)
        override val required = _required.asStateFlow()

        private val _playStore = MutableStateFlow<String?>(null)
        override val playStore = _playStore.asStateFlow()

        private val _directDownload = MutableStateFlow<String?>(null)
        override val directDownload = _directDownload.asStateFlow()

        override fun bind(service: IBinder?): IUpdateService? {
            return IUpdateService.Stub.asInterface(service)
        }

        override fun onConnected(service: IUpdateService) {
            service.hasUpdate(object : IUpdateCheckCallback.Stub() {
                override fun onUpdateInfo(updateInfo: IUpdateInfo?) {
                    _available.update { updateInfo?.available() ?: false }
                    _required.update { updateInfo?.required() ?: false }
                    _playStore.update { updateInfo?.playstore()?.ifBlank { null } }
                    _directDownload.update { updateInfo?.directDownload()?.ifBlank { null } }
                }
            })
        }

        override fun onDisconnected() {
            _available.update { false }
            _required.update { false }
            _playStore.update { null }
            _directDownload.update { null }
        }
    }
}