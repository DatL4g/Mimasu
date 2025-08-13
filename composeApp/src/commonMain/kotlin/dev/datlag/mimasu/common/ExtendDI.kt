package dev.datlag.mimasu.common

import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import io.tolgee.Tolgee
import io.tolgee.common.PlatformTolgee
import org.kodein.di.DirectDI
import org.kodein.di.instanceOrNull

fun DirectDI.firebaseAuthService(): FirebaseAuthService {
    return instanceOrNull<FirebaseAuthService>() ?: FirebaseAuthService()
}

fun DirectDI.firebaseDataSource(): FirebaseAuthDataSource {
    return instanceOrNull<FirebaseAuthDataSource>() ?: FirebaseAuthDataSource(firebaseAuthService())
}

fun Tolgee.Companion.instanceOrInit(config: Tolgee.Config): PlatformTolgee {
    return instanceOrNull ?: init(config).let { instance }
}

fun Tolgee.Companion.instanceOrInit(builder: Tolgee.Config.Builder.() -> Unit): PlatformTolgee {
    return instanceOrNull ?: init(builder).let { instance }
}