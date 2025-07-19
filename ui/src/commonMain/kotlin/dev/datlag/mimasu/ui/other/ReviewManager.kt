package dev.datlag.mimasu.ui.other

import androidx.compose.runtime.Composable

expect class ReviewManager {
    suspend fun requestReview()
}

@Composable
expect fun rememberReviewManager(): ReviewManager