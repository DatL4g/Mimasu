package dev.datlag.mimasu.extension

import android.content.Context
import dev.datlag.mimasu.extension.movie.Request
import dev.datlag.mimasu.extension.movie.WatchInfo
import dev.datlag.mimasu.extension.service.MovieInfoService
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class MovieProvider(context: Context) {

    private val extensionPackages = AIDLService.extensions(
        packageManager = context.packageManager,
        action = MovieInfoService.ACTION
    )

    private val services = extensionPackages.map { packageName ->
        MovieInfoService(context).also { service ->
            AIDLService.bind(context, service, packageName)
        }
    }

    private val boundServices: List<MovieInfoService>
        get() = services.filter { it.isBound }

    fun unbind(context: Context): Boolean {
        return boundServices.map {
            AIDLService.unbind(context, it)
        }.all { it }
    }

    suspend fun requestInfo(request: Request): List<WatchInfo> = coroutineScope {
        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestInfo(request)
            }.getOrNull()
        } }.awaitAll().filterNotNull()
    }

}