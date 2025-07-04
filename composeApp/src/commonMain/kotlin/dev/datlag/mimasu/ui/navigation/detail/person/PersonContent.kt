package dev.datlag.mimasu.ui.navigation.detail.person

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonAKA
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonBiography
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonInfo
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonPosterContent
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography

@Composable
fun PersonContent(
    person: Person,
    initialPeople: People?,
    initialCast: Movie.Credits.Cast?,
    initialCrew: Movie.Credits.Crew?,
    padding: PaddingValues,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = padding
    ) {
        item {
            PersonPosterContent(
                person = person,
                initialPeople = initialPeople,
                initialCast = initialCast,
                initialCrew = initialCrew,
                onBack = onBack,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(
                        top = 32.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 32.dp
                    )
            )
        }
        item {
            Text(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                text = person.name,
                style = Platform.typography().headlineMedium,
                fontWeight = FontWeight.SemiBold,
                softWrap = true
            )
        }
        item {
            PersonInfo(
                person = person,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }
        item {
            PersonBiography(
                person = person,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }
        item {
            PersonAKA(
                person = person,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}