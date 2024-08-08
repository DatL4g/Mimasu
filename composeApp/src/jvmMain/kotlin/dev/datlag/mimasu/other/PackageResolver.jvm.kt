package dev.datlag.mimasu.other

import dev.datlag.mimasu.tv.Package
import dev.datlag.mimasu.tv.PackageAware
import dev.datlag.tooling.Tooling

actual class PackageResolver : PackageAware {
    actual val netflix: Netflix = Netflix()
    actual val disneyPlus: DisneyPlus = DisneyPlus()
    actual val amazonPrimeVideo: AmazonPrimeVideo = AmazonPrimeVideo()
    actual val burningSeries: BurningSeries = BurningSeries()
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

    override val crunchyRollInstalled: Boolean
        get() = crunchyRoll.installed

    override val paramountPlusInstalled: Boolean
        get() = paramountPlus.installed

    actual inner class Netflix : Package {
        actual override val installed: Boolean
            get() = false

        override val brandColor: Int
            get() = 0xFFe50914.toInt()
    }

    actual inner class DisneyPlus : Package {
        actual override val installed: Boolean
            get() = false

        override val brandColor: Int
            get() = 0xFF000000.toInt()
    }

    actual inner class AmazonPrimeVideo : Package {
        actual override val installed: Boolean
            get() = false

        override val brandColor: Int
            get() = 0xFF1399FF.toInt()
    }

    actual inner class BurningSeries : Package {
        actual override val installed: Boolean
            get() = false

        override val brandColor: Int
            get() = 0xFF094576.toInt()
    }

    actual inner class CrunchyRoll : Package {
        actual override val installed: Boolean
            get() = false

        override val brandColor: Int
            get() = 0xFFF47521.toInt()
    }

    actual inner class ParamountPlus : Package {
        actual override val installed: Boolean
            get() = false

        override val brandColor: Int
            get() = 0xFF2864f0.toInt()
    }

}