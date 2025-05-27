package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IMovieInfoProvider
import dev.datlag.mimasu.extension.model.Movie
import dev.datlag.mimasu.extension.movie.Callback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

internal class MovieInfoService(context: Context) : AIDLService<IMovieInfoProvider>(context) {
    override val connectionAction: String = ACTION

    override fun bind(service: IBinder?): IMovieInfoProvider? {
        return IMovieInfoProvider.Stub.asInterface(service)
    }

    override fun onConnected(service: IMovieInfoProvider) { }

    override fun onDisconnected() { }

    suspend fun requestInfo(bytes: ByteArray): Movie.Response = withTimeout(10.seconds) {
        suspendCancellableCoroutine { continuation ->
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
                    val response = Movie.Response(info)

                    continuation.resumeWith(when (response) {
                        null -> Result.failure(IllegalStateException())
                        else -> Result.success(response)
                    })
                }
            })
        }
    }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.IMovieInfoProvider"
    }
}