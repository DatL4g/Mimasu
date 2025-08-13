package dev.datlag.mimasu.ui.navigation.detail.person.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.person_also_known_as
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList

@Composable
fun PersonAKA(
    person: Person,
    modifier: Modifier = Modifier
) {
    val aka = remember(person.id) { person.alsoKnownAs.mapNotNull {
        it.ifBlank { null }
    }.toImmutableList() }

    if (aka.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.person_also_known_as),
                style = Platform.typography().headlineSmall,
                maxLines = 1
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                aka.forEach {
                    ElevatedSuggestionChip(
                        onClick = { },
                        label = {
                            Text(text = it)
                        }
                    )
                }
            }
        }
    }
}