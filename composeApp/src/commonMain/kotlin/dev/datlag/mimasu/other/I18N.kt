package dev.datlag.mimasu.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.vanniktech.locale.Locale
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.common.default
import dev.datlag.mimasu.common.localized
import dev.datlag.mimasu.core.common.sprintf
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.withIOContext
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Buffer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.io.decodeFromSource
import org.jetbrains.compose.resources.StringResource
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

data object I18N {

    private const val BASE_URL = "https://cdn.tolg.ee/"

    private val locale by lazy {
        Locale.default()
    }
    val language by lazy {
        locale.localized()
    }
    val languageCode by lazy {
        locale.language.code
    }
    val region by lazy {
        locale.territory?.code?.ifBlank { null } ?: locale.territory?.code3?.ifBlank { null }
    }

    private var translationCache = mapOf<String, String>()
    private val fallbackJson = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    private val mutex = Mutex()

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun fetchTranslation(client: HttpClient, json: Json?): Map<String, String>? = mutex.withLock {
        if (translationCache.isNotEmpty()) {
            return@withLock null
        }

        suspend fun response(url: String) = suspendCatching {
            client.get(url)
        }.getOrNull()?.let {
            if (it.status.isSuccess()) {
                it
            } else {
                null
            }
        }

        val contentDelivery = Sekret.tolgeeContentDelivery(BuildKonfig.packageName) ?: return null
        val deliveryUrl = "$BASE_URL$contentDelivery/"

        val urls = setOfNotNull(
            "$deliveryUrl$language.json",
            region?.let { "$deliveryUrl$languageCode-$region.json" },
            "$deliveryUrl$languageCode.json"
        )

        val response = urls.firstNotNullOfOrNull { response(it) } ?: return null

        val source = suspendCatching {
            Buffer().also {
                it.write(response.readRawBytes())
            }
        }.getOrNull() ?: return null

        return suspendCatching {
            (json ?: fallbackJson).decodeFromSource<Map<String, String>>(source)
        }.getOrNull()
    }

    private suspend fun translation(key: String, client: HttpClient, json: Json?): String? {
        return if (translationCache.isEmpty()) {
            suspendCatching {
                fetchTranslation(client, json)
            }.getOrNull()?.let {
                translationCache = it
            }

            get(key)
        } else {
            get(key)
        }
    }

    operator fun get(key: String): String? = translationCache[key]

    @Composable
    fun stringResource(res: StringResource): String {
        val client by localDI().instanceOrNull<HttpClient>()
        val json by localDI().instanceOrNull<Json>()

        val data by produceState(
            org.jetbrains.compose.resources.stringResource(res)
        ) {
            value = client?.let {
                withIOContext {
                    translation(res.key, it, json)
                }
            } ?: get(res.key) ?: value
        }
        return data
    }

    @Composable
    fun stringResource(res: StringResource, vararg formatArgs: Any): String {
        val client by localDI().instanceOrNull<HttpClient>()
        val json by localDI().instanceOrNull<Json>()

        val data by produceState(
            org.jetbrains.compose.resources.stringResource(res, *formatArgs)
        ) {
            value = client?.let {
                withIOContext {
                    translation(res.key, it, json)?.sprintf(*formatArgs)
                }
            } ?: get(res.key)?.sprintf(*formatArgs) ?: value
        }
        return data
    }
}