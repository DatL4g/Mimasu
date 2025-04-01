package dev.datlag.mimasu.firebase.config

import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig

data class FirebaseRemoteConfigService(
    private val isDebug: Boolean,
    private val app: FirebaseApp = Firebase.app
) {

    suspend fun config(): FirebaseRemoteConfig = Firebase.remoteConfig(app).apply {
        settings {
            if (isDebug) {

            }
        }
    }.also { it.fetchAndActivate() }

    suspend inline fun <reified T> get(key: String, defaultValue: T): T = suspendCatching<T> {
        config()[key]
    }.getOrNull() ?: defaultValue

    suspend inline fun <reified T> getOrNull(key: String): T? = suspendCatching<T> {
        config()[key]
    }.getOrNull()

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = get(key, defaultValue)
    suspend fun getLong(key: String, defaultValue: Long): Long = get(key, defaultValue)
    suspend fun getString(key: String, defaultValue: String) = get(key, defaultValue)

    suspend fun getBoolean(key: String): Boolean? = getOrNull(key)
    suspend fun getLong(key: String): Long? = getOrNull(key)
    suspend fun getString(key: String): String? = getOrNull<String?>(key)?.ifBlank { null }

}
