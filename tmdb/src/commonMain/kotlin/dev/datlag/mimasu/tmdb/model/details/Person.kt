package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.model.HasKana
import dev.datlag.mimasu.tmdb.model.HasLogo
import dev.datlag.tooling.scopeCatching
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Person(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("also_known_as") val alsoKnownAs: SerializableImmutableSet<String> = persistentSetOf(),
    @SerialName("biography") val biography: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("deathday") val deathday: String? = null,
    @SerialName("gender") val gender: Int = 0,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("id") val id: Int = 0,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("known_for_department") val knownForDepartment: String? = null,
    @SerialName("name") val name: String,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("place_of_birth") val placeOfBirth: String? = null,
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("profile_path") override val logoSource: String? = null
) : HasLogo, HasKana {

    @Transient
    override val kanaSource: String? = name.ifBlank { null }

    @Transient
    override val kanaBackupSource: String? = originalName?.ifBlank { null }

    @Transient
    val birthdayLocalDate = birthday?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }

    @Transient
    val deathdayLocalDate = deathday?.ifBlank { null }?.let { scopeCatching {
        LocalDate.parse(it)
    }.getOrNull() }

    @Transient
    val isFemale = gender == 1

    @Transient
    val isMale = gender == 2

    @Transient
    val isNonBinary = gender == 3

}
