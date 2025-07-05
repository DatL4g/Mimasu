package dev.datlag.mimasu.ui.other

import android.os.Build

data object ArchUtils {

    const val X86 = "x86"
    const val X86_64 = "x86_64"

    @JvmStatic
    fun isX86(fallback: Boolean = false): Boolean {
        val primary = primaryABI() ?: return fallback

        return primary.equals(X86, ignoreCase = true)
    }

    @JvmStatic
    fun isX86_64(fallback: Boolean = false): Boolean {
        val primary = primaryABI() ?: return fallback

        return primary.equals(X86_64, ignoreCase = true)
    }

    @JvmStatic
    fun supportsRive(): Boolean {
        return !isX86(fallback = true) && !isX86_64(fallback = true)
    }

    private fun primaryABI(): String? {
        val supportedABIs = Build.SUPPORTED_ABIS.orEmpty().ifEmpty { return null }
        return supportedABIs.firstOrNull()?.ifBlank { null }
    }
}