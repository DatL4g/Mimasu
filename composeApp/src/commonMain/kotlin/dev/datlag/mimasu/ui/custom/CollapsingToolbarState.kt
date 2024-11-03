package dev.datlag.mimasu.ui.custom

import kotlinx.serialization.Serializable

@Serializable
data class CollapsingToolbarState(
    val expandProgress: Float,
    val isExpanded: Boolean = expandProgress >= 0.99F
) {
    val isCollapsed: Boolean = !isExpanded
}
