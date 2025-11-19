package dev.datlag.mimasu.extension

import android.content.Context
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.extension.model.Movie
import dev.datlag.mimasu.extension.service.MovieService
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class MovieProviderAndroid(private val context: Context) : MovieProvider {

    private var extensionPackages = emptySet<String>()
    private var services = bind(context)
    private val boundServices: List<MovieService>
        get() = services.filter { it.isBound }

    override suspend fun initialize() {
        if (extensionPackages.isEmpty()) {
            extensionPackages = withContext(Dispatchers.IO) {
                AIDLService.extensions(
                    packageManager = context.packageManager,
                    action = MovieService.ACTION
                )
            }
        }
    }

    fun unbind(context: Context): Boolean {
        return boundServices.map {
            AIDLService.unbind(context, it)
        }.all { it }
    }

    private fun bind(context: Context) = extensionPackages.map { packageName ->
        MovieService(context).also { service ->
            AIDLService.bind(context, service, packageName)
        }
    }

    suspend fun rebind(context: Context) {
        withContext(Dispatchers.Main) {
            unbind(context)
        }

        extensionPackages = withContext(Dispatchers.Virtual ?: Dispatchers.IO) {
            AIDLService.extensions(
                packageManager = context.packageManager,
                action = MovieService.ACTION
            )
        }
        services = withContext(Dispatchers.Main) {
            bind(context)
        }
    }

    suspend fun rebindIfNoneAvailable(context: Context) {
        if (boundServices.isEmpty()) {
            rebind(context)
        }
    }

    override suspend fun requestId(request: Movie.Request): Boolean = coroutineScope {
        val bytes = request.toByteArray()

        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestId(request.tmdbId, bytes)
            }.getOrNull()
        } }.awaitAll().filterNotNull().any { it }
    }

    override suspend fun requestStream(tmdbId: Int): Movie.Response? = coroutineScope {
        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestStream(tmdbId)
            }.getOrNull()
        } }.awaitAll().filterNotNull().fold(Movie.Response()) { left, right ->
            left + right
        }.takeUnless { it.isEmpty() }
    }
}