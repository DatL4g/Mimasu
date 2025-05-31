package dev.datlag.mimasu.extension

import android.content.Context
import dev.datlag.mimasu.extension.model.Movie
import dev.datlag.mimasu.extension.service.MovieInfoService
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class MovieProviderAndroid(context: Context) : MovieProvider {

    private var extensionPackages = emptySet<String>() /*AIDLService.extensions(
        packageManager = context.packageManager,
        action = MovieInfoService.ACTION
    )*/

    private var services = bind(context)

    private val boundServices: List<MovieInfoService>
        get() = services.filter { it.isBound }

    fun unbind(context: Context): Boolean {
        return boundServices.map {
            AIDLService.unbind(context, it)
        }.all { it }
    }

    private fun bind(context: Context) = extensionPackages.map { packageName ->
        MovieInfoService(context).also { service ->
            AIDLService.bind(context, service, packageName)
        }
    }

    fun rebind(context: Context) {
        unbind(context)

        extensionPackages = AIDLService.extensions(
            packageManager = context.packageManager,
            action = MovieInfoService.ACTION
        )
        services = bind(context)
    }

    override suspend fun requestInfo(request: Movie.Request): List<Movie.Response> = coroutineScope {
        val bytes = request.toByteArray()

        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestInfo(bytes)
            }.getOrNull()
        } }.awaitAll().filterNotNull()
    }

}