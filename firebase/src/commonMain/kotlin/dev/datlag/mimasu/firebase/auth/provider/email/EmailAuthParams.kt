package dev.datlag.mimasu.firebase.auth.provider.email

import dev.datlag.sekret.Secret

data class EmailAuthParams(
    val email: String,
    @Secret val password: String
)
