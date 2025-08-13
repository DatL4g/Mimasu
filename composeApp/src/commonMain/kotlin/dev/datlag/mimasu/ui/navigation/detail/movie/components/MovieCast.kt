package dev.datlag.mimasu.ui.navigation.detail.movie.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_cast
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MovieCast(
    movie: Movie,
    modifier: Modifier = Modifier,
    onClick: (Movie.Credits.Cast) -> Unit
) {
    val cast = remember(movie.id) { movie.credits?.cast.orEmpty().toImmutableList() }

    if (cast.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                text = stringResource(Res.string.movie_cast),
                style = Platform.typography().headlineSmall,
                maxLines = 1
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cast) {
                    MovieCharacterCard(
                        cast = it,
                        modifier = Modifier.width(100.dp).height(200.dp),
                        onClick = {
                            onClick(it)
                        }
                    )
                }
            }
        }
    }
}