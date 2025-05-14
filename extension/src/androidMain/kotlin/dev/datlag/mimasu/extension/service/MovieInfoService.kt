package dev.datlag.mimasu.extension.service

import android.content.Context
import android.os.IBinder
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.IMovieInfoProvider
import dev.datlag.mimasu.extension.movie.Callback
import dev.datlag.mimasu.extension.movie.Request
import dev.datlag.mimasu.extension.movie.WatchInfo
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

    suspend fun requestInfo(request: Request): WatchInfo = withTimeout(10.seconds) {
        suspendCancellableCoroutine { continuation ->
            if (!isBound) {
                continuation.cancel()
                return@suspendCancellableCoroutine
            }
            val connection = service ?: run {
                continuation.cancel()
                return@suspendCancellableCoroutine
            }
            connection.requestInfo(request, object : Callback.Stub() {
                override fun onResult(watchInfo: WatchInfo?) {
                    continuation.resumeWith(when (watchInfo) {
                        null -> Result.failure(NullPointerException())
                        else -> Result.success(watchInfo)
                    })
                }
            })
        }
    }

    companion object {
        internal const val ACTION = "dev.datlag.mimasu.extension.IMovieInfoProvider"
    }
}