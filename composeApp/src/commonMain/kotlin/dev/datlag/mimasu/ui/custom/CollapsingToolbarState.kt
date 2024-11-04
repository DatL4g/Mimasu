package dev.datlag.mimasu.ui.custom

import kotlinx.serialization.Serializable

/**
 * TopAppBar Collapse State
 *
 * [isExpanded] and [isCollapsed] may be false at the same time
 *
 * @property expandProgress progress of expansion 1F equals fully expanded
 */
@Serializable
data class CollapsingToolbarState(
    val expandProgress: Float,
) {
    /**
     * Check if the TopAppBar is fully expanded (with 0.01F threshold)
     */
    val isExpanded: Boolean = expandProgress >= 0.99F

    /**
     * Check if the TopAppBar is fully collapsed (with 0.01F threshold)
     */
    val isCollapsed: Boolean = expandProgress <= 0.01F
}
