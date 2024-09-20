package dev.datlag.mimasu.other

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import dev.datlag.mimasu.core.MimasuConnection
import dev.datlag.mimasu.core.common.toColorIntCompat
import dev.datlag.mimasu.tv.Package
import dev.datlag.mimasu.tv.PackageAware
import dev.datlag.mimasu.ui.theme.Colors
import dev.datlag.tooling.scopeCatching

actual class PackageResolver(
    private val packageManager: PackageManager
) : PackageAware {
    actual val netflix: Netflix = Netflix()
    actual val disneyPlus: DisneyPlus = DisneyPlus()
    actual val amazonPrimeVideo: AmazonPrimeVideo = AmazonPrimeVideo()
    actual val burningSeries: BurningSeries = BurningSeries()
    actual val mimasuExtension: MimasuExtension = MimasuExtension()
    actual val crunchyRoll: CrunchyRoll = CrunchyRoll()
    actual val paramountPlus: ParamountPlus = ParamountPlus()

    override val netflixInstalled: Boolean
        get() = netflix.installed

    override val disneyPlusInstalled: Boolean
        get() = disneyPlus.installed

    override val amazonPrimeVideoInstalled: Boolean
        get() = amazonPrimeVideo.installed

    override val burningSeriesInstalled: Boolean
        get() = burningSeries.installed

    override val mimasuExtensionInstalled: Boolean
        get() = mimasuExtension.installed

    override val crunchyRollInstalled: Boolean
        get() = crunchyRoll.installed

    override val paramountPlusInstalled: Boolean
        get() = paramountPlus.installed

    constructor(context: Context) : this(context.packageManager)

    actual inner class Netflix : Package {
        private val defaultPackageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(NETFLIX_DEFAULT_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(NETFLIX_DEFAULT_PACKAGE, 0)
            }
        }.getOrNull()

        private val tvPackageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(NETFLIX_TV_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(NETFLIX_TV_PACKAGE, 0)
            }
        }.getOrNull()

        actual override val installed: Boolean
            get() = defaultPackageInfo != null || tvPackageInfo != null

        override val brandColor: Int
            get() = 0xFFe50914.toInt()
    }

    actual inner class DisneyPlus : Package {
        private val packageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(DISNEY_PLUS_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(DISNEY_PLUS_PACKAGE, 0)
            }
        }.getOrNull()

        actual override val installed: Boolean
            get() = packageInfo != null

        override val brandColor: Int
            get() = 0xFF000000.toInt()
    }

    actual inner class AmazonPrimeVideo : Package {
        private val defaultPackageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(AMAZON_PRIME_VIDEO_DEFAULT_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(AMAZON_PRIME_VIDEO_DEFAULT_PACKAGE, 0)
            }
        }.getOrNull()

        private val tvPackageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(AMAZON_PRIME_VIDEO_TV_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(AMAZON_PRIME_VIDEO_TV_PACKAGE, 0)
            }
        }.getOrNull()

        actual override val installed: Boolean
            get() = defaultPackageInfo != null || tvPackageInfo != null

        override val brandColor: Int
            get() = 0xFF1399FF.toInt()
    }

    actual inner class BurningSeries : Package {
        private val packageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(BURNING_SERIES_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(BURNING_SERIES_PACKAGE, 0)
            }
        }.getOrNull() ?: scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(OLD_BURNING_SERIES_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(OLD_BURNING_SERIES_PACKAGE, 0)
            }
        }.getOrNull()

        actual override val installed: Boolean
            get() = packageInfo != null

        override val brandColor: Int
            get() = 0xFF094576.toInt()
    }

    actual inner class MimasuExtension : Package {
        private val packageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(MIMASU_EXTENSION_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(MIMASU_EXTENSION_PACKAGE, 0)
            }
        }.getOrNull()

        actual override val installed: Boolean
            get() = packageInfo != null

        override val brandColor: Int
            get() {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Colors.THEME_LIGHT_PRIMARY.toColorInt()
                } else {
                    Colors.THEME_LIGHT_PRIMARY.toColorIntCompat()
                }
            }
    }

    actual inner class CrunchyRoll : Package {
        private val packageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(CRUNCHY_ROLL_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(CRUNCHY_ROLL_PACKAGE, 0)
            }
        }.getOrNull()

        actual override val installed: Boolean
            get() = packageInfo != null

        override val brandColor: Int
            get() = 0xFFF47521.toInt()
    }

    actual inner class ParamountPlus : Package {
        private val packageInfo: PackageInfo? = scopeCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(PARAMOUNT_PLUS_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(PARAMOUNT_PLUS_PACKAGE, 0)
            }
        }.getOrNull()

        actual override val installed: Boolean
            get() = packageInfo != null

        override val brandColor: Int
            get() = 0xFF2864f0.toInt()
    }

    companion object {
        private const val NETFLIX_DEFAULT_PACKAGE = "com.netflix.mediaclient"
        private const val NETFLIX_TV_PACKAGE = "com.netflix.ninja"

        private const val DISNEY_PLUS_PACKAGE = "com.disney.disneyplus"

        private const val AMAZON_PRIME_VIDEO_DEFAULT_PACKAGE = "com.amazon.avod.thirdpartyclient"
        private const val AMAZON_PRIME_VIDEO_TV_PACKAGE = "com.amazon.amazonvideo.livingroom"

        private const val BURNING_SERIES_PACKAGE = "dev.datlag.burningseries"
        private const val OLD_BURNING_SERIES_PACKAGE = "de.datlag.burningseries"
        private const val MIMASU_EXTENSION_PACKAGE = "dev.datlag.mimasu.extension"

        private const val CRUNCHY_ROLL_PACKAGE = "com.crunchyroll.crunchyroid"

        private const val PARAMOUNT_PLUS_PACKAGE = "com.cbs.ca"

        /**
         * Get all available packageNames implementing the extension.
         */
        fun extensions(packageManager: PackageManager): List<String> {
            val intent = Intent(MimasuConnection.CONNECTION_ACTION)
            val resolveInfoList = scopeCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.queryIntentServices(intent, PackageManager.ResolveInfoFlags.of(0))
                } else {
                    packageManager.queryIntentServices(intent, 0)
                }
            }.getOrNull() ?: return emptyList()

            return resolveInfoList.mapNotNull {
                it.serviceInfo?.packageName?.ifBlank { null } ?: it.resolvePackageName?.ifBlank { null }
            }.toSet().sortedWith(compareBy(
                { it != MIMASU_EXTENSION_PACKAGE },
                { it != BURNING_SERIES_PACKAGE },
                { it != CRUNCHY_ROLL_PACKAGE },
                { it != DISNEY_PLUS_PACKAGE },
                { it != NETFLIX_DEFAULT_PACKAGE },
                { it != NETFLIX_TV_PACKAGE },
                { it != AMAZON_PRIME_VIDEO_DEFAULT_PACKAGE },
                { it != AMAZON_PRIME_VIDEO_TV_PACKAGE },
                { it != PARAMOUNT_PLUS_PACKAGE }
            ))
        }

        /**
         * Get all available packageNames implementing the extension.
         */
        fun extensions(context: Context) = extensions(
            context.packageManager
            ?: context.applicationContext.packageManager
        )

        /**
         * Binds the watch extension from any available provider.
         *
         * @return [Boolean] whether the service could be bound.
         */
        fun bindExtension(context: Context): Boolean {
            val availablePackages = extensions(
                context.packageManager ?: context.applicationContext.packageManager
            ).ifEmpty { return MimasuConnection.bound.value }

            var index = 0
            var bound = false
            while (index < availablePackages.size && !MimasuConnection.bound.value) {
                val bindIntent = Intent(MimasuConnection.CONNECTION_ACTION).apply {
                    setPackage(availablePackages[index])
                }
                val result = scopeCatching {
                    context.bindService(bindIntent, MimasuConnection, Context.BIND_AUTO_CREATE)
                }
                if (result.isSuccess) {
                    bound = true
                    break
                } else if (result.isFailure) {
                    bound = false
                }

                index++
            }
            return bound || MimasuConnection.bound.value
        }
    }

}