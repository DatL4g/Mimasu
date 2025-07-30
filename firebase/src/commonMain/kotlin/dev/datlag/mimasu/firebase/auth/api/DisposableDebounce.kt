package dev.datlag.mimasu.firebase.auth.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Url
import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.mimasu.firebase.auth.model.DisposableInfo
import io.ktor.client.HttpClient

interface DisposableDebounce {

    @GET
    suspend fun checkDisposable(
        @Url url: String = BASE_URL,
        @Query("email") email: String
    ): DisposableInfo

    @GET
    suspend fun disposableDomains(
        @Url url: String = DOMAINS_URL
    ): Set<String>

    companion object {
        const val BASE_URL = "https://disposable.debounce.io/"

        const val DOMAINS_URL = "https://disposable.github.io/disposable-email-domains/domains.json"

        fun create(client: HttpClient): DisposableDebounce {
            return ktorfit {
                baseUrl(BASE_URL)
                httpClient(client)
            }.createDisposableDebounce()
        }
    }
}