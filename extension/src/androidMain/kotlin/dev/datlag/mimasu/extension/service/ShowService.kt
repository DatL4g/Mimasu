package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IShowInfoProvider
import dev.datlag.mimasu.extension.model.Show
import dev.datlag.mimasu.extension.show.EpisodeCallback
import dev.datlag.mimasu.extension.show.ShowCallback
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.suspendCancellableCoroutine

internal class ShowService(context: Context) : AIDLService<IShowInfoProvider>(context) {
    override val connectionAction: String = ACTION

    private val mappedIds by atomic<MutableMap<Int, Int>>(mutableMapOf())

    override fun bind(service: IBinder?): IShowInfoProvider? {
        return IShowInfoProvider.Stub.asInterface(service)
    }

    override fun onConnected(service: IShowInfoProvider) { }

    override fun onDisconnected() { }

    suspend fun requestId(tmdbId: Int?, bytes: ByteArray): Boolean = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val connection = service ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        connection.requestShowId(bytes, object : ShowCallback.Stub() {
            override fun onResult(id: Int) {
                if (tmdbId != null) {
                    mappedIds[tmdbId] = id
                }
                continuation.resumeWith(Result.success(tmdbId != null))
            }
        })
    }

    suspend fun requestEpisode(tmdbId: Int, bytes: ByteArray): Show.Response = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val connection = service ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val id = mappedIds[tmdbId] ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        connection.requestEpisode(id, bytes, object : EpisodeCallback.Stub() {
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