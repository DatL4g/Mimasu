package dev.datlag.mimasu.tmdb.model

import dev.datlag.tooling.wanakana.WanaKana

interface HasKana {

    val kanaSource: String?
    val kanaBackupSource: String?

    val kanaSourceIsJapanese: Boolean
        get() = kanaSource.let {
            !it.isNullOrBlank() && WanaKana.isJapanese(it)
        }

    val kanaBackupSourceIsJapanese: Boolean
        get() = kanaBackupSource.let {
            !it.isNullOrBlank() && WanaKana.isJapanese(it)
        }

    val kanaSourceRomaji: String?
        get() = kanaSource.let {
            if (kanaSourceIsJapanese && !it.isNullOrBlank()) {
                WanaKana.toRomaji(it).trim().takeUnless { r ->
                    WanaKana.hasJapanese(r)
                }?.ifBlank { it } ?: it
            } else {
                it
            }
        }

    val kanaBackupSourceRomaji: String?
        get() = kanaBackupSource.let {
            if (kanaBackupSourceIsJapanese && !it.isNullOrBlank()) {
                WanaKana.toRomaji(it).trim().takeUnless { r ->
                    WanaKana.hasJapanese(r)
                }?.ifBlank { it } ?: it
            } else {
                it
            }
        }
}