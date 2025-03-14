package dev.datlag.mimasu.tmdb

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.Response
import dev.datlag.mimasu.tmdb.model.TV
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ResponseTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `serialize Movie to Json`() {
        val movie: Response = Movie(id = 1, title = "Test")
        val serialized = json.encodeToString(movie)
        val expected = """{"id":1,"title":"Test","media_type":"movie"}"""

        assertThat(serialized).isEqualTo(expected)
    }

    @Test
    fun `deserialize Movie from Json`() {
        val jsonContent = """{"id": 1, "title": "Test", "media_type": "movie"}"""
        val deserialized = json.decodeFromString<Response>(jsonContent)

        assertThat(deserialized).isInstanceOf(Movie::class)
    }

    @Test
    fun `serialize TV to Json`() {
        val movie: Response = TV(id = 1, name = "Test")
        val serialized = json.encodeToString(movie)
        val expected = """{"id":1,"name":"Test","media_type":"tv"}"""

        assertThat(serialized).isEqualTo(expected)
    }

    @Test
    fun `deserialize TV from Json`() {
        val jsonContent = """{"id": 1, "name": "Test", "media_type": "tv"}"""
        val deserialized = json.decodeFromString<Response>(jsonContent)

        assertThat(deserialized).isInstanceOf(TV::class)
    }

    @Test
    fun `serialize People to Json`() {
        val movie: Response = People(id = 1, name = "Test")
        val serialized = json.encodeToString(movie)
        val expected = """{"id":1,"name":"Test","media_type":"person"}"""

        assertThat(serialized).isEqualTo(expected)
    }

    @Test
    fun `deserialize People from Json`() {
        val jsonContent = """{"id": 1, "name": "Test", "media_type": "person"}"""
        val deserialized = json.decodeFromString<Response>(jsonContent)

        assertThat(deserialized).isInstanceOf(People::class)
    }
}