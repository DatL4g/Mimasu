package dev.datlag.mimasu.other

import coil3.annotation.ExperimentalCoilApi
import coil3.network.ConnectivityChecker
import dev.datlag.tooling.compose.ioDispatcher
import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

@OptIn(ExperimentalCoilApi::class)
actual object Connection : ConnectivityChecker {
    private val connectivity = Connectivity {
        autoStart = true
    }
    @OptIn(DelicateCoroutinesApi::class)
    private val updates = connectivity.statusUpdates.stateIn(
        scope = GlobalScope + ioDispatcher(),
        started = SharingStarted.Eagerly,
        initialValue = Connectivity.Status.Disconnected
    )

    actual override fun isOnline(): Boolean {
        return if (connectivity.isMonitoring.value) {
            updates.value is Connectivity.Status.Connected
        } else {
            true
        }
    }
}