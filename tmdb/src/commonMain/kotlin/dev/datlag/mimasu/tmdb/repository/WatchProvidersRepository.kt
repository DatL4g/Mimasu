package dev.datlag.mimasu.tmdb.repository

import dev.datlag.mimasu.tmdb.api.WatchProviders
import dev.datlag.sekret.Secret
import dev.datlag.tooling.async.suspendCatching
import io.ktor.client.call.body
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.Serializable
import kotlin.concurrent.Volatile

class WatchProvidersRepository(
    @Secret private val apiKey: String,
    private val watchProviders: WatchProviders,
    private val language: String
) {

    @Volatile
    private var movieProviders: ImmutableSet<WatchProviders.Response.Provider> = persistentSetOf()

    @Volatile
    private var tvProviders: ImmutableSet<WatchProviders.Response.Provider> = persistentSetOf()

    suspend fun movieProvider(name: String): WatchProviders.Response.Provider? {
        if (movieProviders.isEmpty()) {
            movieProviders = suspendCatching {
                watchProviders.movie(apiKey, language).body<WatchProviders.Response>().results
            }.getOrNull() ?: movieProviders
        }

        return movieProviders.firstOrNull {
            it.name.equals(name, ignoreCase = true)
        } ?: KnownProviders.matchingMovie(name)?.toProvider()
    }

    suspend fun tvProvider(name: String): WatchProviders.Response.Provider? {
        if (tvProviders.isEmpty()) {
            tvProviders = suspendCatching {
                watchProviders.movie(apiKey, language).body<WatchProviders.Response>().results
            }.getOrNull() ?: tvProviders
        }

        return tvProviders.firstOrNull {
            it.name.equals(name, ignoreCase = true)
        } ?: KnownProviders.matchingTV(name)?.toProvider()
    }

    @Serializable
    sealed interface KnownProviders {

        val id: Int
        val name: String

        fun toProvider() = WatchProviders.Response.Provider(
            id = id,
            name = name
        )

        fun matchName(name: String): Boolean {
            return name.equals(this.name, ignoreCase = true)
        }

        @Serializable
        sealed interface Movie : KnownProviders {

            @Serializable
            data object Netflix : Movie {
                override val id: Int = 8
                override val name: String = "Netflix"
            }

            @Serializable
            data object DisneyPlus : Movie {
                override val id: Int = 337
                override val name: String = "Disney+"

                override fun matchName(name: String): Boolean {
                    return super.matchName(name) || name.equals("Disney Plus", ignoreCase = true)
                }
            }

            @Serializable
            data object AmazonPrimeVideo : Movie {
                override val id: Int = 9
                override val name: String = "Amazon Prime Video"
            }

            @Serializable
            data object Crunchyroll : Movie {
                override val id: Int = 283
                override val name: String = "Crunchyroll"
            }

            @Serializable
            data object ParamountPlus : Movie {
                override val id: Int = 531
                override val name: String = "Paramount+"

                override fun matchName(name: String): Boolean {
                    return super.matchName(name) || name.equals("Paramount Plus", ignoreCase = true)
                }
            }
        }

        @Serializable
        sealed interface TV : KnownProviders {

            @Serializable
            data object Netflix : TV {
                override val id: Int = 8
                override val name: String = "Netflix"
            }

            @Serializable
            data object DisneyPlus : TV {
                override val id: Int = 337
                override val name: String = "Disney+"

                override fun matchName(name: String): Boolean {
                    return super.matchName(name) || name.equals("Disney Plus", ignoreCase = true)
                }
            }

            @Serializable
            data object AmazonPrimeVideo : TV {
                override val id: Int = 9
                override val name: String = "Amazon Prime Video"
            }

            @Serializable
            data object Crunchyroll : TV {
                override val id: Int = 283
                override val name: String = "Crunchyroll"
            }

            @Serializable
            data object ParamountPlus : TV {
                override val id: Int = 531
                override val name: String = "Paramount+"

                override fun matchName(name: String): Boolean {
                    return super.matchName(name) || name.equals("Paramount Plus", ignoreCase = true)
                }
            }
        }

        companion object {
            fun matchingMovie(name: String): KnownProviders.Movie? {
                return when {
                    Movie.Netflix.matchName(name) -> Movie.Netflix
                    Movie.DisneyPlus.matchName(name) -> Movie.DisneyPlus
                    Movie.AmazonPrimeVideo.matchName(name) -> Movie.AmazonPrimeVideo
                    Movie.Crunchyroll.matchName(name) -> Movie.Crunchyroll
                    Movie.ParamountPlus.matchName(name) -> Movie.ParamountPlus
                    else -> null
                }
            }

            fun matchingTV(name: String): KnownProviders.TV? {
                return when {
                    TV.Netflix.matchName(name) -> TV.Netflix
                    TV.DisneyPlus.matchName(name) -> TV.DisneyPlus
                    TV.AmazonPrimeVideo.matchName(name) -> TV.AmazonPrimeVideo
                    TV.Crunchyroll.matchName(name) -> TV.Crunchyroll
                    TV.ParamountPlus.matchName(name) -> TV.ParamountPlus
                    else -> null
                }
            }
        }
    }
}