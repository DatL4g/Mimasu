package dev.datlag.mimasu.ui.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class ReviewManager {
    actual suspend fun requestReview() { }
}

@Composable
actual fun rememberReviewManager(): ReviewManager {
    return remember { ReviewManager() }
}