package dev.datlag.mimasu

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import dev.datlag.mimasu.common.toExpressiveTypography
import dev.datlag.mimasu.core.Virtual
import dev.datlag.mimasu.firebase.config.FirebaseRemoteConfigService
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.mimasu.ui.other.Network
import dev.datlag.mimasu.ui.theme.Font
import dev.datlag.tooling.compose.TargetIO
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instanceOrNull
import kotlin.getValue

@OptIn(ExperimentalComposeUiApi::class, DelicateCoilApi::class, DelicateCoroutinesApi::class)
fun main() {
    Firebase.initialize(
        context = null,
        options = FirebaseOptions(
            apiKey = Sekret.firebaseWebApiKey(BuildKonfig.packageName)!!,
            projectId = Sekret.projectId(BuildKonfig.packageName),
            gcmSenderId = Sekret.projectNumber(BuildKonfig.packageName),
            applicationId = Sekret.firebaseWebId(BuildKonfig.packageName)!!,
            authDomain = Sekret.firebaseAuthDomain(BuildKonfig.packageName)
        )
    )
    val di = DI {
        import(NetworkModule.di)

        bindSingleton<FirebaseRemoteConfigService> {
            FirebaseRemoteConfigService(isDebug = false)
        }
    }
    val imageLoader by di.instanceOrNull<ImageLoader>()
    imageLoader?.let(SingletonImageLoader::setUnsafe)

    val config by di.instanceOrNull<FirebaseRemoteConfigService>()
    GlobalScope.launch(Dispatchers.Virtual ?: Dispatchers.TargetIO) {
        Network.fetchConfig(config ?: error("No firebase remote config found."))
    }

    ComposeViewport(viewportContainerId = "composeRoot") {
        App(
            di = di,
            typography = Font.manrope().toExpressiveTypography()
        )
    }
}