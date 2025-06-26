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

    suspend inline fun <reified T> get(key: String, defaultValue: T): Result<T> = suspendCatching<T> {
        config()[key] ?: defaultValue
    }.recover { defaultValue }

    suspend inline fun <reified T> getOrNull(key: String): Result<T> = suspendCatching {
        config()[key]
    }

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = get(key, defaultValue).getOrDefault(defaultValue)
    suspend fun getLong(key: String, defaultValue: Long): Long = get(key, defaultValue).getOrDefault(defaultValue)
    suspend fun getString(key: String, defaultValue: String) = get(key, defaultValue).getOrDefault(defaultValue)

    suspend fun getBoolean(key: String): Boolean? = getOrNull<Boolean?>(key).getOrNull()
    suspend fun getLong(key: String): Long? = getOrNull<Long?>(key).getOrNull()

    suspend fun getStringResult(key: String): Result<String> = getOrNull<String?>(key).mapCatching {
        if (it.isNullOrBlank()) {
            throw NullPointerException("Remote String for '$key' is null or empty")
        } else {
            it
        }
    }

    suspend fun getNullableString(key: String) = getOrNull<String?>(key).getOrNull()

}
