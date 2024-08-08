package dev.datlag.mimasu.other

import dev.datlag.mimasu.tv.PackageAware
import dev.datlag.mimasu.tv.Package

expect class PackageResolver : PackageAware {

    val netflix: Netflix
    val disneyPlus: DisneyPlus
    val amazonPrimeVideo: AmazonPrimeVideo
    val burningSeries: BurningSeries
    val crunchyRoll: CrunchyRoll
    val paramountPlus: ParamountPlus

    inner class Netflix : Package {
        override val installed: Boolean
    }

    inner class DisneyPlus : Package {
        override val installed: Boolean
    }

    inner class AmazonPrimeVideo : Package {
        override val installed: Boolean
    }

    inner class BurningSeries : Package {
        override val installed: Boolean
    }

    inner class CrunchyRoll : Package {
        override val installed: Boolean
    }

    inner class ParamountPlus : Package {
        override val installed: Boolean
    }
}