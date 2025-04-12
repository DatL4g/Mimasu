package dev.datlag.mimasu.firebase.auth.provider.email

import dev.datlag.sekret.Secret
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.EmailAuthProvider

data class EmailAuthParams(
    val email: String,
    @Secret val password: String
) {
    fun asCredential(): AuthCredential = EmailAuthProvider.credential(
        email = email,
        password = password
    )
}