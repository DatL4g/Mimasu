package dev.datlag.mimasu.ui.navigation.detail.person

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.common.formatMedium
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.person_birthday
import dev.datlag.mimasu.composeapp.generated.resources.person_birthday_date_format
import dev.datlag.mimasu.composeapp.generated.resources.person_deathday
import dev.datlag.mimasu.composeapp.generated.resources.person_deathday_date_format
import dev.datlag.mimasu.composeapp.generated.resources.person_gender
import dev.datlag.mimasu.composeapp.generated.resources.person_gender_female
import dev.datlag.mimasu.composeapp.generated.resources.person_gender_male
import dev.datlag.mimasu.composeapp.generated.resources.person_gender_non_binary
import dev.datlag.mimasu.composeapp.generated.resources.person_gender_unknown
import dev.datlag.mimasu.composeapp.generated.resources.person_known_for
import dev.datlag.mimasu.composeapp.generated.resources.person_place_of_birth
import dev.datlag.mimasu.tmdb.common.logos
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tolgee.stringResource
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
            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(
                        top = 32.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 32.dp
                    )
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
            Column(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                person.knownForDepartment?.ifBlank { null }?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.WORK,
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.weight(1F),
                            text = stringResource(Res.string.person_known_for)
                        )
                        Text(
                            modifier = Modifier.weight(2F),
                            text = it
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.PERSON,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.weight(1F),
                        text = stringResource(Res.string.person_gender)
                    )
                    Text(
                        modifier = Modifier.weight(2F),
                        text = when {
                            person.isFemale -> stringResource(Res.string.person_gender_female)
                            person.isMale -> stringResource(Res.string.person_gender_male)
                            person.isNonBinary -> stringResource(Res.string.person_gender_non_binary)
                            else -> stringResource(Res.string.person_gender_unknown)
                        }
                    )
                }
                person.birthdayLocalDate.formatMedium(
                    fallbackFormat = Res.string.person_birthday_date_format,
                    fallbackValue = person.birthday
                )?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.CAKE,
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.weight(1F),
                            text = stringResource(Res.string.person_birthday)
                        )
                        Text(
                            modifier = Modifier.weight(2F),
                            text = it
                        )
                    }
                }
                person.deathdayLocalDate.formatMedium(
                    fallbackFormat = Res.string.person_deathday_date_format,
                    fallbackValue = person.deathday
                )?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.DECEASED,
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.weight(1F),
                            text = stringResource(Res.string.person_deathday)
                        )
                        Text(
                            modifier = Modifier.weight(2F),
                            text = it
                        )
                    }
                }
                person.placeOfBirth?.ifBlank { null }?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.GLOBE_LOCATION_PIN,
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.weight(1F),
                            text = stringResource(Res.string.person_place_of_birth)
                        )
                        Text(
                            modifier = Modifier.weight(2F),
                            text = it
                        )
                    }
                }
            }
        }
    }
}