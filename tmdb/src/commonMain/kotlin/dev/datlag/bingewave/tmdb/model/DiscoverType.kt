package dev.datlag.bingewave.tmdb.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface DiscoverType {

    @Serializable
    sealed interface Movie : DiscoverType {

        // ToDo("replace with better naming if available")
        @Serializable
        sealed class Release : Movie, Number() {

            abstract val value: Int

            override fun toByte(): Byte = value.toByte()
            override fun toDouble(): Double = value.toDouble()
            override fun toFloat(): Float = value.toFloat()
            override fun toInt(): Int = value
            override fun toLong(): Long = value.toLong()
            override fun toShort(): Short = value.toShort()

            @Serializable
            data object One : Release() {
                override val value: Int = 1
            }

            @Serializable
            data object Two : Release() {
                override val value: Int = 2
            }

            @Serializable
            data object Three : Release() {
                override val value: Int = 3
            }

            @Serializable
            data object Four : Release() {
                override val value: Int = 4
            }

            @Serializable
            data object Five : Release() {
                override val value: Int = 5
            }

            @Serializable
            data object Six : Release() {
                override val value: Int = 6
            }
        }
    }

    @Serializable
    sealed interface Tv : DiscoverType {

        @Serializable
        sealed class Status : Tv, Number() {

            abstract val value: Int

            override fun toByte(): Byte = value.toByte()
            override fun toDouble(): Double = value.toDouble()
            override fun toFloat(): Float = value.toFloat()
            override fun toInt(): Int = value
            override fun toLong(): Long = value.toLong()
            override fun toShort(): Short = value.toShort()

            @Serializable
            data object Zero : Status() {
                override val value: Int = 0
            }

            @Serializable
            data object One : Status() {
                override val value: Int = 1
            }

            @Serializable
            data object Two : Status() {
                override val value: Int = 2
            }

            @Serializable
            data object Three : Status() {
                override val value: Int = 3
            }

            @Serializable
            data object Four : Status() {
                override val value: Int = 4
            }

            @Serializable
            data object Five : Status() {
                override val value: Int = 5
            }
        }

        @Serializable
        sealed class Type : Tv, Number() {

            abstract val value: Int

            override fun toByte(): Byte = value.toByte()
            override fun toDouble(): Double = value.toDouble()
            override fun toFloat(): Float = value.toFloat()
            override fun toInt(): Int = value
            override fun toLong(): Long = value.toLong()
            override fun toShort(): Short = value.toShort()

            @Serializable
            data object Zero : Type() {
                override val value: Int = 0
            }

            @Serializable
            data object One : Type() {
                override val value: Int = 1
            }

            @Serializable
            data object Two : Type() {
                override val value: Int = 2
            }

            @Serializable
            data object Three : Type() {
                override val value: Int = 3
            }

            @Serializable
            data object Four : Type() {
                override val value: Int = 4
            }

            @Serializable
            data object Five : Type() {
                override val value: Int = 5
            }

            @Serializable
            data object Six : Type() {
                override val value: Int = 6
            }
        }
    }

    @Serializable
    sealed class Monetization : DiscoverType, CharSequence {

        abstract val value: String

        override val length: Int
            get() = value.length

        override operator fun get(index: Int): Char {
            return value[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return value.subSequence(startIndex, endIndex)
        }

        override fun toString(): String {
            return value
        }

        @Serializable
        data object Flatrate : Monetization() {
            override val value: String = "flatrate"
        }

        @Serializable
        data object Free : Monetization() {
            override val value: String = "free"
        }

        @Serializable
        data object Ads : Monetization() {
            override val value: String = "ads"
        }

        @Serializable
        data object Rent : Monetization() {
            override val value: String = "rent"
        }

        @Serializable
        data object Buy : Monetization() {
            override val value: String = "buy"
        }
    }
}