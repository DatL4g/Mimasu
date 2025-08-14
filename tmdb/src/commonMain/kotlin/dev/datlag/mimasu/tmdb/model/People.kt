package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@ConsistentCopyVisibility
@OptIn(ExperimentalSerializationApi::class)
data class People internal constructor(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("id") override val id: Int,
    @SerialName("name") val name: String,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("media_type") @EncodeDefault(EncodeDefault.Mode.ALWAYS) override val mediaType: String? = "person",
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("gender") val gender: Int = 0,
    @SerialName("known_for_department") val knownForDepartment: String? = null,
    @SerialName("profile_path") override val logoSource: String? = null,
    @SerialName("known_for") private val knownFor: SerializableImmutableSet<Response> = persistentSetOf(),
): Response, HasLogo, HasKana {

    @Transient
    override val kanaSource: String? = name.ifBlank { null }

    @Transient
    override val kanaBackupSource: String? = originalName?.ifBlank { null }

    @Transient
    val knownForMovie = knownFor.filterIsInstance<Movie>().toImmutableList()

    @Transient
    val knownForTV = knownFor.filterIsInstance<TV>().toImmutableList()

    @Transient
    val isFemale = gender == 1

    @Transient
    val isMale = gender == 2

    @Transient
    val isNonBinary = gender == 3
}
