package dev.datlag.mimasu.other

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import dev.datlag.mimasu.common.deleteRecursivelySafely
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.service.SpaceService
import dev.datlag.tooling.scopeCatching
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

class SpaceManager(
    private val context: Context
) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val storageStatsManager = context.getSystemService<StorageStatsManager>() ?: scopeCatching {
        context.getSystemService(Context.STORAGE_STATS_SERVICE) as? StorageStatsManager
    }.getOrNull() ?: ContextCompat.getSystemService(context, StorageStatsManager::class.java)

    private val storageManager = context.getSystemService<StorageManager>() ?: scopeCatching {
        context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
    }.getOrNull() ?: ContextCompat.getSystemService(context, StorageManager::class.java)

    private val activityManager = context.getSystemService<ActivityManager>() ?: scopeCatching {
        context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    }.getOrNull() ?: ContextCompat.getSystemService(context, ActivityManager::class.java)

    private val extensionSpace by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        SpaceService.bind(context)
    }

    private val _sizes = MutableStateFlow<Sizes?>(null)
    val sizes = _sizes.asStateFlow()

    val extensionSizes by lazy { extensionSpace?.space ?: MutableStateFlow(null) }
    val extensionAvailable by lazy {
        extensionSpace != null || AIDLService.extensionInstalled(context)
    }

    suspend fun loadSizes() {
        _sizes.update {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                modernSizes() ?: legacySizes() ?: it
            } else {
                legacySizes() ?: it
            }
        }
    }

    suspend fun clearCache(): Boolean {
        return context.cacheDir.deleteRecursivelySafely().also {
            loadSizes()
        }
    }

    suspend fun clearApplicationData(): Boolean {
        return activityManager?.clearApplicationUserData()?.let {
            it && clearCache()
        } ?: clearCache()
    }

    fun extensionClearCache() {
        extensionSpace?.takeIf {
            it.clearCache()
        } ?: AIDLService.extensionStorageSettings(context)
    }

    fun extensionClearStorage(): Boolean {
        val result = extensionSpace?.clearStorage() ?: false

        if (!result) {
            AIDLService.extensionStorageSettings(context)
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun modernSizes(): Sizes? {
        val appSpecificStorageUuid = scopeCatching {
            storageManager?.getUuidForPath(context.filesDir)
        }.getOrNull() ?: return null
        val user = Process.myUserHandle()

        return scopeCatching {
            val storageStats = storageStatsManager?.queryStatsForPackage(
                appSpecificStorageUuid,
                context.packageName,
                user
            ) ?: return@scopeCatching null

            Sizes(
                _app = storageStats.appBytes,
                _userData = storageStats.dataBytes,
                _cache = storageStats.cacheBytes
            ).takeUnless { it.isEmpty() }
        }.getOrNull()
    }

    private suspend fun legacySizes(): Sizes? = coroutineScope {
        val userData = async {
            context.filesDir.walkTopDown().sumOf {
                scopeCatching {
                    it.length()
                }.getOrNull() ?: 0
            }
        }
        val cache = async {
            context.cacheDir.walkTopDown().sumOf {
                scopeCatching {
                    it.length()
                }.getOrNull() ?: 0
            }
        }

        return@coroutineScope Sizes(
            _app = 0,
            _userData = userData.await(),
            _cache = cache.await()
        ).takeUnless { it.isEmpty() }
    }

    @Serializable
    data class Sizes(
        private val _app: Long,
        private val _userData: Long,
        private val _cache: Long
    ) {
        val app: Long = _app.takeIf { it > 0 } ?: 0
        val userData: Long = _userData.takeIf { it > 0 } ?: 0
        val cache: Long = _cache.takeIf { it > 0 } ?: 0
        val total: Long = app + userData + cache

        fun isEmpty(): Boolean {
            return total <= 0
        }
    }
}