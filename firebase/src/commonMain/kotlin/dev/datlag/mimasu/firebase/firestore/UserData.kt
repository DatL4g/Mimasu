package dev.datlag.mimasu.firebase.firestore

import dev.gitlive.firebase.firestore.BaseTimestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName(ADULT) val adult: Boolean = false,
    @SerialName(PREMIUM) val premium: Boolean = false,
    @SerialName(GITHUB_TOKEN) val githubToken: String? = null,
    @SerialName(GITHUB_UPDATED) val githubUpdated: BaseTimestamp? = null
) {

    companion object {
        const val COLLECTION = "user"

        const val ADULT = "adult"
        const val PREMIUM = "premium"
        const val GITHUB_TOKEN = "githubToken"
        const val GITHUB_UPDATED = "githubUpdated"

        val Default = UserData(
            adult = false,
            premium = false,
            githubToken = null,
            githubUpdated = null
        )
    }
}
