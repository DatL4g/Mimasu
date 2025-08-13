package dev.datlag.mimasu.ui.navigation.detail.show.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.justwatch
import dev.datlag.mimasu.composeapp.generated.resources.show_justwatch_ads
import dev.datlag.mimasu.composeapp.generated.resources.show_justwatch_attribution
import dev.datlag.mimasu.composeapp.generated.resources.show_justwatch_buy
import dev.datlag.mimasu.composeapp.generated.resources.show_justwatch_flatrate
import dev.datlag.mimasu.composeapp.generated.resources.show_justwatch_free
import dev.datlag.mimasu.composeapp.generated.resources.show_justwatch_rent
import dev.datlag.mimasu.tmdb.common.logos
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.common.header
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowWatchProviderDialog(
    watchProviders: Show.WatchProviders.Providers,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val uriHandler = LocalUriHandler.current

                    IconButton(
                        onClick = onDismiss
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.ARROW_BACK_IOS_NEW,
                            contentDescription = null,
                        )
                    }
                    Text(
                        modifier = Modifier.weight(1F),
                        text = stringResource(Res.string.justwatch),
                        style = Platform.typography().headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = {
                            watchProviders.link?.let(uriHandler::openUri)
                        },
                        enabled = !watchProviders.link.isNullOrBlank()
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.OPEN_IN_BROWSER,
                            contentDescription = null,
                        )
                    }
                }
            }
            header {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    text = stringResource(Res.string.show_justwatch_attribution),
                    textAlign = TextAlign.Center,
                )
            }
            watchProviders.free.ifEmpty { null }?.toImmutableList()?.let { free ->
                header {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        text = stringResource(Res.string.show_justwatch_free),
                        style = Platform.typography().titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
                items(free) { provider ->
                    Provider(
                        info = provider,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            watchProviders.ads.ifEmpty { null }?.toImmutableList()?.let { ads ->
                header {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        text = stringResource(Res.string.show_justwatch_ads),
                        style = Platform.typography().titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
                items(ads) { provider ->
                    Provider(
                        info = provider,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            watchProviders.flatrate.ifEmpty { null }?.toImmutableList()?.let { flatrate ->
                header {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        text = stringResource(Res.string.show_justwatch_flatrate),
                        style = Platform.typography().titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
                items(flatrate) { provider ->
                    Provider(
                        info = provider,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            watchProviders.buy.ifEmpty { null }?.toImmutableList()?.let { buy ->
                header {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        text = stringResource(Res.string.show_justwatch_buy),
                        style = Platform.typography().titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
                items(buy) { provider ->
                    Provider(
                        info = provider,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            watchProviders.rent.ifEmpty { null }?.toImmutableList()?.let { rent ->
                header {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        text = stringResource(Res.string.show_justwatch_rent),
                        style = Platform.typography().titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
                items(rent) { provider ->
                    Provider(
                        info = provider,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun Provider(
    info: Show.WatchProviders.Providers.Info,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.defaultMinSize(minHeight = ButtonDefaults.MinHeight, minWidth = ButtonDefaults.MinWidth),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val logos = remember(info) { info.logos() }

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
            ),
            contentScale = ContentScale.Inside
        )
        Text(text = info.providerName)
    }
}