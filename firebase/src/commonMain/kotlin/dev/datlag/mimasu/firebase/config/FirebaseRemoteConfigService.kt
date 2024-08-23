package dev.datlag.mimasu.firebase.config

import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig

class FirebaseRemoteConfigService(
    private val isDebug: Boolean,
    private val app: FirebaseApp = Firebase.app
) {

    suspend fun config(): FirebaseRemoteConfig = Firebase.remoteConfig(app).apply {
        if (isDebug) {
            settings {
                // Force refresh to 10 seconds when in Debug
                minimumFetchIntervalInSeconds = 10
            }
        }
    }.also { it.fetchAndActivate() }

    suspend inline fun <reified T> get(key: String, defaultValue: T): T = suspendCatching<T> {
        config()[key]
    }.getOrNull() ?: defaultValue

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = get(key, defaultValue)

    suspend fun getLong(key: String, defaultValue: Long): Long = get(key, defaultValue)

    suspend fun getString(key: String, defaultValue: String) = get(key, defaultValue)

}