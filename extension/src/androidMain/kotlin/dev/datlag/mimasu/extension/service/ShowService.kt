package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IShowInfoProvider
import dev.datlag.mimasu.extension.model.Show
import dev.datlag.mimasu.extension.show.EpisodeCallback
import dev.datlag.mimasu.extension.show.ShowCallback
import dev.datlag.mimasu.extension.show.StreamCallback
import dev.datlag.tooling.scopeCatching
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap

internal class ShowService(context: Context) : AIDLService<IShowInfoProvider>(context) {
    override val connectionAction: String = ACTION

    private val mappedIds = ConcurrentHashMap<Int, Int>()

    override fun bind(service: IBinder?): IShowInfoProvider? {
        return scopeCatching {
            IShowInfoProvider.Stub.asInterface(service)
        }.getOrNull()
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

    suspend fun requestEpisode(tmdbId: Int, bytes: ByteArray): Boolean = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val connection = service ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val showId = mappedIds[tmdbId] ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        connection.requestEpisodeAvailability(showId, bytes, object : EpisodeCallback.Stub() {
            override fun onResult(available: Boolean) {
                continuation.resumeWith(Result.success(available))
            }
        })
    }

    suspend fun requestStream(tmdbId: Int, bytes: ByteArray): Show.Response? = suspendCancellableCoroutine { continuation ->
        if (!isBound) {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val connection = service ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        val showId = mappedIds[tmdbId] ?: run {
            continuation.cancel()
            return@suspendCancellableCoroutine
        }
        connection.requestStream(showId, bytes, object : StreamCallback.Stub() {
            override fun onResult(info: ByteArray?) {
                val response = Show.Response(info)

                continuation.resumeWith(when (response) {
                    null -> Result.failure(IllegalArgumentException("Malformed response"))
                    else -> Result.success(response)
                })
            }
        })
    }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.IShowInfoProvider"
    }
}