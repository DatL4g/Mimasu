package dev.datlag.mimasu

import android.content.Context
import androidx.multidex.MultiDexApplication
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import dev.datlag.mimasu.BuildConfig
import dev.datlag.mimasu.firebase.config.FirebaseRemoteConfigService
import dev.datlag.mimasu.firebase.initializeFirebase
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.sekret.NativeLoader
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.launchIO
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class App : MultiDexApplication(), DIAware {

    private val applicationScope = CoroutineScope(ioDispatcher() +  SupervisorJob())

    override val di: DI = DI {
        bindSingleton<Context> {
            applicationContext
        }
        bindSingleton<FirebaseRemoteConfigService> {
            FirebaseRemoteConfigService(
                isDebug = BuildConfig.DEBUG
            )
        }

        import(NetworkModule.di)
    }

    @OptIn(DelicateCoilApi::class)
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }

        val imageLoader by di.instance<ImageLoader>()
        SingletonImageLoader.setUnsafe(imageLoader)

        if (NativeLoader.loadLibrary("sekret")) {
            val appId = Sekret.firebaseAndroidApplication(BuildKonfig.packageName)
            val apiKey = Sekret.firebaseAndroidApiKey(BuildKonfig.packageName)

            if (appId.isNullOrBlank() || apiKey.isNullOrBlank()) {
                NetworkModule.initializeFailure()
                return
            }

            initializeFirebase(
                projectId = Sekret.firebaseProject(BuildKonfig.packageName),
                applicationId = appId,
                apiKey = apiKey
            )
        } else {
            NetworkModule.initializeFailure()
            return
        }

        val config by di.instance<FirebaseRemoteConfigService>()

        applicationScope.launchIO {
            NetworkModule.fetchConfig(config)
        }
    }
}