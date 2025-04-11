package dev.datlag.mimasu

import android.content.Context
import androidx.multidex.MultiDexApplication
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.google.android.gms.net.CronetProviderInstaller
import dev.datlag.mimasu.firebase.config.FirebaseRemoteConfigService
import dev.datlag.mimasu.module.NetworkModule
import dev.datlag.sekret.NativeLoader
import dev.datlag.tooling.compose.ioDispatcher
import dev.datlag.tooling.compose.launchIO
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.instanceOrNull

class App : MultiDexApplication(), DIAware {

    private val applicationScope = CoroutineScope(ioDispatcher() + SupervisorJob())

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

        val imageLoader by di.instanceOrNull<ImageLoader>()
        imageLoader?.let(SingletonImageLoader::setUnsafe)

        CronetProviderInstaller.installProvider(this)

        if (AppInitializer.isSekretLoaded(applicationContext)) {
            val appId = Sekret.firebaseAppId(BuildKonfig.packageName)
            val apiKey = Sekret.firebaseApiKey(BuildKonfig.packageName)

            if (appId.isNullOrBlank() || apiKey.isNullOrBlank()) {
                NetworkModule.initializeFailure()
                return
            }

            Firebase.initialize(
                context = this,
                options = FirebaseOptions(
                    projectId = Sekret.projectId(BuildKonfig.packageName),
                    applicationId = appId,
                    apiKey = apiKey
                )
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

    override fun onTerminate() {
        super.onTerminate()

        applicationScope.cancel()
    }

}