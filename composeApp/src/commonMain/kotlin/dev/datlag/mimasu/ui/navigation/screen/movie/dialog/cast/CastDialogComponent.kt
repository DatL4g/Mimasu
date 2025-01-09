package dev.datlag.mimasu.ui.navigation.screen.movie.dialog.cast

import dev.datlag.mimasu.tmdb.api.Details
import dev.datlag.mimasu.ui.navigation.DialogComponent

interface CastDialogComponent : DialogComponent {
    val cast: Details.Movie.Credits.Cast
}