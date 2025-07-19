package dev.datlag.mimasu.ui.other

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import co.touchlab.kermit.Logger
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import dev.datlag.mimasu.ui.common.findActivity
import dev.datlag.tooling.async.suspendCatching

actual class ReviewManager(
    private val manager: com.google.android.play.core.review.ReviewManager,
    private val activity: Activity?
) {
    actual suspend fun requestReview() {
        if (activity == null) {
            return
        }

        val info = suspendCatching {
            manager.requestReview()
        }.getOrNull() ?: return

        suspendCatching {
            manager.launchReview(
                activity = activity,
                reviewInfo = info
            )
        }
    }
}

@Composable
actual fun rememberReviewManager(): ReviewManager {
    val view = LocalView.current
    val context = view.context ?: LocalContext.current
    val activity: Activity? = LocalActivity.current ?: view.context?.findActivity() ?: LocalContext.current.findActivity()

    val manager = remember(context) {
        ReviewManagerFactory.create(context)
    }

    return remember(manager, activity) {
        ReviewManager(
            manager = manager,
            activity = activity
        )
    }
}