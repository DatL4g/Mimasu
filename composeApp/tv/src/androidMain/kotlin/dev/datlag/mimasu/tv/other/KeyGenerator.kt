package dev.datlag.mimasu.tv.other

import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

data object KeyGenerator {
    val alphabet by lazy { ('A'..'Z').toImmutableSet().toImmutableList() }
    val specialCharV1 by lazy { persistentSetOf('-', '\'').toImmutableList() }
    val specialCharV2 by lazy { persistentSetOf('_', ',').toImmutableList() }
    val specialCharV3 by lazy { persistentSetOf('&', '?', '!', '%', ':', '.', ';', '#', '+').toImmutableList() }
    val alphabetLower by lazy { ('a'..'z').toImmutableSet().toImmutableList() }
    val numbers by lazy { ('0'..'9').toImmutableSet().toImmutableList() }
}