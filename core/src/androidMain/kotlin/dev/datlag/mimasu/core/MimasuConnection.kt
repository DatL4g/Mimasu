package dev.datlag.mimasu.core

import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import dev.datlag.mimasu.core.model.UpdateInfo
import dev.datlag.mimasu.core.update.IUpdateCheckCallback
import dev.datlag.mimasu.core.update.IUpdateInfo
import dev.datlag.mimasu.core.update.IUpdateService
import dev.datlag.tooling.scopeCatching
import io.github.aakira.napier.Napier
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

    class Update internal constructor(context: Context) : AIDLService<IUpdateService>(context), UpdateInfo {
        override val connectionAction: String = "dev.datlag.mimasu.core.update.IUpdateService"

        private val _available = MutableStateFlow(false)
        override val available = _available.asStateFlow()

        private val _required = MutableStateFlow(false)
        override val required = _required.asStateFlow()

        private val _storeURL = MutableStateFlow<String?>(null)
        override val storeURL = _storeURL.asStateFlow()

        private val _directDownload = MutableStateFlow<String?>(null)
        override val directDownload = _directDownload.asStateFlow()

        override fun bind(service: IBinder?): IUpdateService? {
            return IUpdateService.Stub.asInterface(service)
        }

        override fun onConnected(service: IUpdateService) {
            service.hasUpdate(object : IUpdateCheckCallback.Stub() {
                override fun onUpdateInfo(updateInfo: IUpdateInfo?) {
                    _storeURL.update {
                        scopeCatching {
                            updateInfo?.storeURL()
                        }.getOrNull()?.ifBlank { null }
                    }
                    _directDownload.update {
                        scopeCatching {
                            updateInfo?.directDownload()
                        }.getOrNull()?.ifBlank { null }
                    }

                    _available.update {
                        scopeCatching {
                            updateInfo?.available()
                        }.getOrNull() ?: false
                    }
                    _required.update {
                        scopeCatching {
                            updateInfo?.required()
                        }.getOrNull() ?: false
                    }
                }
            })
        }

        override fun onDisconnected() {
            _available.update { false }
            _required.update { false }
            _storeURL.update { null }
            _directDownload.update { null }

            instances.remove(this)
        }

        companion object {
            private val instances = mutableListOf<Update>()

            val bound: Update
                get() = instances.first()

            fun unbind(wrapper: ContextWrapper, instance: Update?) {
                scopeCatching {
                    if (instance != null) {
                        wrapper.unbindService(instance)
                    }
                }.onSuccess {
                    instances.remove(instance)
                }
            }

            fun unbindAll(wrapper: ContextWrapper) {
                instances.forEach {
                    unbind(wrapper, it)
                }
            }

            fun bind(context: Context): Update {
                return instances.firstOrNull() ?: Update(context).also { instances.add(it) }
            }
        }
    }
}