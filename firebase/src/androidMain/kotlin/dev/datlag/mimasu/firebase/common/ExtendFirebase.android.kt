package dev.datlag.mimasu.firebase.common

import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException

actual val FirebaseAuthUserCollisionException.commonEmail: String?
    get() = this.email?.ifBlank { null }