package dev.datlag.mimasu.tmdb.model

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed class TrendingWindow : CharSequence {

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
    data object Day : TrendingWindow() {
        override val value: String = "day"

        override fun toString(): String {
            return value
        }
    }

    @Serializable
    data object Week : TrendingWindow() {
        override val value: String = "week"

        override fun toString(): String {
            return value
        }
    }

    companion object {
        fun isTypeOf(value: KClass<*>): Boolean {
            return when {
                value == TrendingWindow::class || value == Day::class || value == Week::class -> true
                else -> false
            }
        }
    }
}