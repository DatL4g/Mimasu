package dev.datlag.mimasu.extension

import android.content.Context
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.extension.model.Show
import dev.datlag.mimasu.extension.service.ShowService
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class ShowProviderAndroid(private val context: Context) : ShowProvider {

    private var extensionPackages = emptySet<String>()

    private var services = bind(context)

    private val boundServices: List<ShowService>
        get() = services.filter { it.isBound }

    override suspend fun initialize() {
        if (extensionPackages.isEmpty()) {
            extensionPackages = withContext(Dispatchers.IO) {
                AIDLService.extensions(
                    packageManager = context.packageManager,
                    action = ShowService.ACTION
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
        ShowService(context).also { service ->
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
                action = ShowService.ACTION
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

    override suspend fun requestId(request: Show.Request): Boolean = coroutineScope {
        val bytes = request.toByteArray()

        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestId(request.tmdbId, bytes)
            }.getOrNull()
        } }.awaitAll().filterNotNull().any { it }
    }

    override suspend fun requestEpisode(tmdbId: Int, request: Show.EpisodeRequest): Boolean = coroutineScope {
        val bytes = request.toByteArray()

        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestEpisode(tmdbId, bytes)
            }.getOrNull()
        } }.awaitAll().filterNotNull().any { it }
    }

    override suspend fun requestStream(tmdbId: Int, request: Show.EpisodeRequest): Show.Response? = coroutineScope {
        val bytes = request.toByteArray()

        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestStream(tmdbId, bytes)
            }.getOrNull()
        } }.awaitAll().filterNotNull().fold(Show.Response()) { left, right ->
            left + right
        }.takeUnless { it.isEmpty() }
    }
}