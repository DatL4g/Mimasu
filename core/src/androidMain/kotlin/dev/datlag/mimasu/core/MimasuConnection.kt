package dev.datlag.mimasu.core

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object MimasuConnection : ServiceConnection {
    const val CONNECTION_ACTION = "dev.datlag.mimasu.core.IMimasuService"
    private var boundService: IMimasuService? = null

    private val _isVideoPlayerSecure = MutableStateFlow(boundService?.isVideoPlayerSecure ?: true)
    val isVideoPlayerSecure: StateFlow<Boolean> = _isVideoPlayerSecure.asStateFlow()

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.boundService = IMimasuService.Stub.asInterface(service)

        _isVideoPlayerSecure.update { boundService?.isVideoPlayerSecure ?: true }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        this.boundService = null

        _isVideoPlayerSecure.update { true }
    }
}