package dev.datlag.mimasu.ui.navigation.detail.person

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonAKA
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonBiography
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonInfo
import dev.datlag.mimasu.ui.navigation.detail.person.components.PersonPosterContent
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography
import org.jetbrains.compose.resources.stringResource

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