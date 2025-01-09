package dev.datlag.mimasu.ui.navigation.screen.movie.dialog.cast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.ComponentContext
import dev.datlag.mimasu.tmdb.api.Details
import io.github.aakira.napier.Napier
import org.kodein.di.DI

class CastDialogScreenComponent(
    componentContext: ComponentContext,
    override val cast: Details.Movie.Credits.Cast,
    override val di: DI,
    private val onDismiss: () -> Unit
) : CastDialogComponent, ComponentContext by componentContext {

    @Composable
    override fun renderCommon() {
        onRender {
            CastDialogScreen(this)
        }
    }

    override fun dismiss() {
        onDismiss()
    }
}