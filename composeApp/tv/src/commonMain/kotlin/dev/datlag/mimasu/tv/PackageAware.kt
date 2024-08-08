package dev.datlag.mimasu.tv

interface PackageAware {

    val netflixInstalled: Boolean
    val disneyPlusInstalled: Boolean
    val amazonPrimeVideoInstalled: Boolean
    val burningSeriesInstalled: Boolean
    val crunchyRollInstalled: Boolean
    val paramountPlusInstalled: Boolean
}