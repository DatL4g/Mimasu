package dev.datlag.mimasu.ui.navigation.detail.person.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.tmdb.common.logos
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols

@Composable
fun PersonPosterContent(
    person: Person,
    initialPeople: People?,
    initialCast: Movie.Credits.Cast?,
    initialCrew: Movie.Credits.Crew?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        val logos = remember(person.id, initialPeople?.id, initialCast?.id, initialCrew?.id) {
            person.logos(initialPeople, initialCast, initialCrew)
        }

        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onBack
        ) {
            MaterialSymbols(
                name = MaterialSymbols.ARROW_BACK_IOS_NEW,
                contentDescription = null
            )
        }
        AsyncImage(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .align(Alignment.Center),
            model = logos.firstOrNull(),
            contentScale = ContentScale.Crop,
            contentDescription = person.name,
            error = rememberNestedImagePainter(
                models = logos.drop(1),
                contentScale = ContentScale.Crop
            )
        )
    }
}