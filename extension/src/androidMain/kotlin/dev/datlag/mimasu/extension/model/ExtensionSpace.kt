package dev.datlag.mimasu.extension.model

data class ExtensionSpace(
    val app: Long,
    val user: Long,
    val cache: Long
) {
    val total: Long = app + user + cache

    fun isEmpty(): Boolean {
        return total <= 0
    }
}
