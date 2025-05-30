package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IShowInfoProvider
import dev.datlag.mimasu.extension.model.Show
import dev.datlag.mimasu.extension.show.Callback
import kotlinx.coroutines.suspendCancellableCoroutine

internal class ShowService(context: Context) : AIDLService<IShowInfoProvider>(context) {
    override val connectionAction: String = ACTION

    override fun bind(service: IBinder?): IShowInfoProvider? {
        return IShowInfoProvider.Stub.asInterface(service)
    }

    override fun onConnected(service: IShowInfoProvider) { }

    override fun onDisconnected() { }

    suspend fun requestInfo(bytes: ByteArray): Show.Response = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val connection = service ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        connection.requestInfo(bytes, object : Callback.Stub() {
            override fun onResult(info: ByteArray?) {
                val response = Show.Response(info)

                continuation.resumeWith(when (response) {
                    null -> Result.failure(IllegalStateException())
                    else -> Result.success(response)
                })
            }
        })
    }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.IShowInfoProvider"
    }
}