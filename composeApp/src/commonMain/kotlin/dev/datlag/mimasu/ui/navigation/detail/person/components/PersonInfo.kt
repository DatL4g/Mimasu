package dev.datlag.mimasu.ui.navigation.detail.person.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.mimasu.ui.common.formatMedium
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import io.tolgee.stringResource

@Composable
fun PersonInfo(
    person: Person,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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