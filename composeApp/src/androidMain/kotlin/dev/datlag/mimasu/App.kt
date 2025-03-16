package dev.datlag.mimasu

import android.content.Context
import androidx.multidex.MultiDexApplication
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.google.android.gms.net.CronetProviderInstaller
import dev.datlag.mimasu.module.NetworkModule
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instanceOrNull

class App : MultiDexApplication(), DIAware {

    override val di: DI = DI {
        bindSingleton<Context> {
            applicationContext
        }

        import(NetworkModule.di)
    }

    @OptIn(DelicateCoilApi::class)
    override fun onCreate() {
        super.onCreate()

        val imageLoader by di.instanceOrNull<ImageLoader>()
        imageLoader?.let(SingletonImageLoader::setUnsafe)

        CronetProviderInstaller.installProvider(this)
    }

}