package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IMovieProvider
import dev.datlag.mimasu.extension.movie.MovieCallback
import dev.datlag.tooling.scopeCatching
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap

internal class MovieService(context: Context) : AIDLService<IMovieProvider>(context) {
    override val connectionAction: String = ACTION

    private val mappedIds = ConcurrentHashMap<Int, Int>()

    override fun bind(service: IBinder?): IMovieProvider? {
        return scopeCatching {
            IMovieProvider.Stub.asInterface(service)
        }.getOrNull()
    }

    override fun onConnected(service: IMovieProvider) { }

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

        connection.requestMovieId(bytes, object : MovieCallback.Stub() {
            override fun onResult(id: Int, available: Boolean) {
                if (tmdbId != null) {
                    mappedIds[tmdbId] = id
                }

                continuation.resumeWith(Result.success(tmdbId != null && available))
            }
        })
    }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.IMovieProvider"
    }
}