package dev.datlag.mimasu.firebase.firestore

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    @SerialName(ADULT) val adult: Boolean = false,
    @SerialName(PREMIUM) val premium: Boolean = false
) {

    companion object {
        const val COLLECTION = "user"

        const val ADULT = "adult"
        const val PREMIUM = "premium"

        val Default = UserData(
            adult = false,
            premium = false
        )
    }
}
