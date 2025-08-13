package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.justwatch
import dev.datlag.mimasu.composeapp.generated.resources.movie_watch
import dev.datlag.mimasu.tmdb.common.logos
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import io.tolgee.stringResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MovieWatchProviderFAB(
    movie: Movie?,
    onWatchClick: () -> Unit
) {
    val regionProviders = remember(movie?.id, movie?.watchProviders) {
        movie?.watchProviders?.providerFor(Locale.current.region)
    }

    if (regionProviders != null) {
        Box {
            val bestProvider = remember(regionProviders) {
                regionProviders.free.firstOrNull()
                    ?: regionProviders.ads.firstOrNull()
                    ?: regionProviders.flatrate.firstOrNull()
                    ?: regionProviders.buy.firstOrNull()
                    ?: regionProviders.rent.firstOrNull()
            }
            val uriHandler = LocalUriHandler.current
            val openLink = remember(regionProviders) {
                regionProviders.link?.ifBlank { null }
            }
            var dialog by remember { mutableStateOf(false) }

            if (dialog) {
                MovieWatchProviderDialog(
                    watchProviders = regionProviders,
                    onDismiss = { dialog = false }
                )
            }

            SmallExtendedFloatingActionButton(
                onClick = {
                    if (regionProviders.hasProviders()) {
                        dialog = !dialog
                    } else {
                        openLink?.let(uriHandler::openUri)
                    }
                },
                icon = {
                    if (bestProvider != null) {
                        val logos = remember(bestProvider) { bestProvider.logos() }
                        var fallback by remember(bestProvider) { mutableStateOf(!bestProvider.hasLogo) }

                        if (fallback) {
                            MaterialSymbols(
                                name = MaterialSymbols.PLAY_ARROW,
                                contentDescription = null,
                                filled = true
                            )
                        } else {
                            AsyncImage(
                                modifier = Modifier.height(24.dp).clip(Platform.shapes().small),
                                model = logos.firstOrNull(),
                                contentDescription = null,
                                placeholder = MaterialSymbols.rememberPainter(
                                    name = MaterialSymbols.PLAY_ARROW,
                                    filled = true
                                ),
                                error = rememberNestedImagePainter(
                                    models = logos.drop(1),
                                    contentScale = ContentScale.Inside,
                                    onError = {
                                        fallback = true
                                    }
                                ),
                                contentScale = ContentScale.Inside
                            )
                        }
                    } else {
                        Icon(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(Res.drawable.justwatch),
                            contentDescription = stringResource(Res.string.justwatch)
                        )
                    }
                },
                text = {
                    if (bestProvider != null) {
                        Text(
                            text = bestProvider.providerName.ifBlank { null } ?: stringResource(Res.string.movie_watch),
                            maxLines = 1
                        )
                    } else {
                        Text(text = stringResource(Res.string.justwatch))
                    }
                }
            )

            if (bestProvider != null) {
                Badge(
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-8).dp),
                    containerColor = Platform.colorScheme().secondary
                ) {
                    Icon(
                        modifier = Modifier.size(12.dp),
                        painter = painterResource(Res.drawable.justwatch),
                        contentDescription = stringResource(Res.string.justwatch)
                    )
                    Text(text = stringResource(Res.string.justwatch))
                }
            }
        }
    }
}