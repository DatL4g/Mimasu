package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.DeadObjectException
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.ISpaceProvider
import dev.datlag.mimasu.extension.model.ExtensionSpace
import dev.datlag.mimasu.extension.space.SpaceCallback
import dev.datlag.tooling.scopeCatching
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SpaceService(private val context: Context) : AIDLService<ISpaceProvider>(context) {
    override val connectionAction: String = ACTION

    private val _space = MutableStateFlow<ExtensionSpace?>(null)
    val space = _space.asStateFlow()

    private val spaceCallback = object : SpaceCallback.Stub() {
        override fun onResult(app: Long, user: Long, cache: Long) {
            _space.update {
                ExtensionSpace(
                    app = app,
                    user = user,
                    cache = cache
                ).takeUnless { e -> e.isEmpty() } ?: it
            }
        }
    }

    override fun bind(service: IBinder?): ISpaceProvider? {
        return scopeCatching {
            ISpaceProvider.Stub.asInterface(service)
        }.getOrNull()
    }

    override fun onConnected(service: ISpaceProvider) {
        scopeCatching {
            service.requestSpace(spaceCallback)
        }.onFailure {
            if (it is DeadObjectException) {
                unbind(context, this)
            }
        }
    }

    override fun onDisconnected() {
        _space.update { null }
    }

    fun clearCache(): Boolean {
        return service?.clearCache(spaceCallback) ?: false
    }

    fun clearStorage(): Boolean {
        return service?.clearStorage(spaceCallback) ?: false
    }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.ISpaceProvider"

        fun bind(context: Context): SpaceService? {
            return SpaceService(context).takeIf { service ->
                AIDLService.bind(context, service, AIDLService.EXTENSION_PACKAGE)
            }
        }
    }
}