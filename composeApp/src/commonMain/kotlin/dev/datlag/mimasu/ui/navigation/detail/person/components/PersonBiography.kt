package dev.datlag.mimasu.ui.navigation.detail.person.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.person_biography
import dev.datlag.mimasu.tmdb.model.details.Person
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@Composable
fun PersonBiography(
    person: Person,
    modifier: Modifier = Modifier
) {
    val biography = remember(person.id) {
        person.biography?.ifBlank { null }
    }

    if (!biography.isNullOrBlank()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var expanded by rememberSaveable(person.id) { mutableStateOf(false) }
            var expandable by remember(person.id) { mutableStateOf(false) }

            Text(
                text = stringResource(Res.string.person_biography),
                style = Platform.typography().headlineSmall,
                maxLines = 1
            )
            SelectionContainer {
                Text(
                    text = biography,
                    softWrap = true,
                    maxLines = if (expanded) Int.MAX_VALUE else 3,
                    overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                    onTextLayout = { result ->
                        if (!expanded) {
                            expandable = result.hasVisualOverflow
                        }
                    }
                )
            }
            if (expandable) {
                IconButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    MaterialSymbols(
                        name = if (expanded) MaterialSymbols.KEYBOARD_ARROW_UP else MaterialSymbols.KEYBOARD_ARROW_DOWN,
                        contentDescription = null
                    )
                }
            }
        }
    }
}