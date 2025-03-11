package dev.datlag.mimasu.tmdb

import assertk.assertThat
import assertk.assertions.isEqualTo
import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tmdb.api.createTrending
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TrendingTest {

    private val trending: Trending = ktorfit {
        baseUrl(TMDB.BASE_URL)
        converterFactories(TimeWindow.Converter)
        httpClient(createMockHttpClient())
    }.createTrending()

    @Test
    fun `movies calls API correctly`() = runTest {
        val response = trending.movies(
            apiKey = "<none>",
            window = TimeWindow.Day,
            language = "en-US",
            page = 0
        )

        assertThat(response.status).isEqualTo(HttpStatusCode.NoContent)
    }

    @Test
    fun `people calls API correctly`() = runTest {
        val response = trending.people(
            apiKey = "<none>",
            window = TimeWindow.Day,
            language = "en-US",
            page = 0
        )

        assertThat(response.status).isEqualTo(HttpStatusCode.NoContent)
    }

    @Test
    fun `tv calls API correctly`() = runTest {
        val response = trending.tv(
            apiKey = "<none>",
            window = TimeWindow.Day,
            language = "en-US",
            page = 0
        )

        assertThat(response.status).isEqualTo(HttpStatusCode.NoContent)
    }

    private fun createMockHttpClient(): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    val correctConversion = request.url.segments.lastOrNull().let {
                        it == TimeWindow.Day.value || it == TimeWindow.Week.value
                    }

                    respond(
                        content = """{"results": []}""",
                        status = if (correctConversion) HttpStatusCode.NoContent else HttpStatusCode.ExpectationFailed,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }
    }
}