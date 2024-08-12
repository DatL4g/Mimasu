package dev.datlag.mimasu.other

import coil3.annotation.ExperimentalCoilApi
import coil3.network.ConnectivityChecker

@OptIn(ExperimentalCoilApi::class)
expect object Connection : ConnectivityChecker {
    override fun isOnline(): Boolean
}