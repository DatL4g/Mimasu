package dev.datlag.mimasu.extension

import android.content.Context
import dev.datlag.mimasu.extension.model.Show
import dev.datlag.mimasu.extension.service.ShowService
import dev.datlag.tooling.async.suspendCatching
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ShowProviderAndroid(context: Context) : ShowProvider {

    private var extensionPackages = AIDLService.extensions(
        packageManager = context.packageManager,
        action = ShowService.ACTION
    )

    private var services = bind(context)

    private val boundServices: List<ShowService>
        get() = services.filter { it.isBound }

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

    fun rebind(context: Context) {
        unbind(context)

        extensionPackages = AIDLService.extensions(
            packageManager = context.packageManager,
            action = ShowService.ACTION
        )
        services = bind(context)
    }

    override suspend fun requestInfo(request: Show.Request): List<Show.Identifier> = coroutineScope {
        val bytes = request.toByteArray()

        return@coroutineScope boundServices.map { async {
            suspendCatching {
                it.requestInfo(bytes)
            }.getOrNull()?.let { id ->
                Show.Identifier(id, it.appPackageName)
            }
        } }.awaitAll().filterNotNull()
    }
}