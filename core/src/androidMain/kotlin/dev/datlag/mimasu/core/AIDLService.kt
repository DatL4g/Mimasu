package dev.datlag.mimasu.core

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class AIDLService<T : IInterface> : ServiceConnection {

    abstract val connectionAction: String

    var service: T? = null
        private set

    private val _bound = MutableStateFlow(false)
    val bound: StateFlow<Boolean> = _bound.asStateFlow()

    val isBound: Boolean
        get() = bound.value

    private val _packageName = MutableStateFlow<String?>(null)
    val packageName: StateFlow<String?> = _packageName.asStateFlow()

    val boundPackageName: String?
        get() = packageName.value

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val bound = bind(service).also {
            this.service = it
        }
        _bound.update { this.service != null }
        _packageName.update { name?.packageName?.ifBlank { null } }
        if (bound != null) {
            onConnected(bound)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
        _bound.update { this.service != null }
        _packageName.update { null }
        onDisconnected()
    }

    abstract fun bind(service: IBinder?): T?
    abstract fun onConnected(service: T)
    abstract fun onDisconnected()
}