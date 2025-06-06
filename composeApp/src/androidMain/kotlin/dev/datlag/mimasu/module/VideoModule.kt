package dev.datlag.mimasu.module

import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import okio.FileSystem
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

@UnstableApi
object VideoModule {

    private const val NAME = "AndroidVideoModule"

    val di: DI.Module = DI.Module(NAME) {
        bindSingleton<DatabaseProvider> {
            StandaloneDatabaseProvider(instance())
        }
        bindSingleton<Cache> {
            SimpleCache(
                (FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "video").toFile(),
                LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024),
                instance()
            )
        }
    }
}