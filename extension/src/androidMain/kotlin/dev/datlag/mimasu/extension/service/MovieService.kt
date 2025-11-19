package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IMovieProvider
import dev.datlag.mimasu.extension.model.Movie
import dev.datlag.mimasu.extension.movie.MovieCallback
import dev.datlag.mimasu.extension.movie.StreamCallback
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

    suspend fun requestStream(tmdbId: Int?): Movie.Response? = suspendCancellableCoroutine { continuation ->
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

        connection.requestStream(showId, object : StreamCallback.Stub() {
            override fun onResult(info: ByteArray?) {
                val response = Movie.Response(info)

                continuation.resumeWith(when (response) {
                    null -> Result.failure(IllegalArgumentException("Malformed response"))
                    else -> Result.success(response)
                })
            }
        })
    }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.IMovieProvider"
    }
}