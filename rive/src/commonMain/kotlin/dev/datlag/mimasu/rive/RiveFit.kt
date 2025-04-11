package dev.datlag.mimasu.rive

import kotlinx.serialization.Serializable

@Serializable
sealed interface RiveFit {

    @Serializable
    data object Fill : RiveFit

    @Serializable
    data object Contain : RiveFit

    @Serializable
    data object Cover : RiveFit

    @Serializable
    sealed interface Fit : RiveFit {

        @Serializable
        data object Width : Fit

        @Serializable
        data object Height : Fit
    }

    @Serializable
    data object None : RiveFit

    @Serializable
    data object ScaleDown : RiveFit

    @Serializable
    data object Layout : RiveFit

}