package dev.datlag.mimasu.firebase.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisposableInfo(
    @SerialName("disposable") val disposable: Boolean = false
)
