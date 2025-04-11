package dev.datlag.mimasu.rive

import kotlinx.serialization.Serializable

@Serializable
sealed interface RiveAlignment {

    @Serializable
    sealed interface Top : RiveAlignment {

        @Serializable
        data object Left : Top

        @Serializable
        data object Center : Top

        @Serializable
        data object Right : Top
    }

    @Serializable
    open class Center : RiveAlignment {

        @Serializable
        data object Left : Center()

        companion object : Center()

        @Serializable
        data object Right : Center()
    }

    @Serializable
    sealed interface Bottom : RiveAlignment {

        @Serializable
        data object Left : Bottom

        @Serializable
        data object Center : Bottom

        @Serializable
        data object Right : Bottom
    }
}