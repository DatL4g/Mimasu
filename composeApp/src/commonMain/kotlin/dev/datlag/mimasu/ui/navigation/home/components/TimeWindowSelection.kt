package dev.datlag.mimasu.ui.navigation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.home_today
import dev.datlag.mimasu.composeapp.generated.resources.home_week
import dev.datlag.mimasu.tmdb.model.trending.TimeWindow
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimeWindowSelection(
    selected: TimeWindow,
    modifier: Modifier = Modifier,
    selectDay: () -> Unit,
    selectWeek: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        ToggleButton(
            checked = selected is TimeWindow.Day,
            onCheckedChange = {
                selectDay()
            },
            modifier = Modifier.weight(1F).semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
        ) {
            AnimatedVisibility(
                visible = selected is TimeWindow.Day
            ) {
                MaterialSymbols(
                    modifier = Modifier.size(ToggleButtonDefaults.IconSize),
                    name = MaterialSymbols.CHECK,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
            }
            Text(
                text = stringResource(Res.string.home_today),
                maxLines = 1
            )
        }
        ToggleButton(
            checked = selected is TimeWindow.Week,
            onCheckedChange = {
                selectWeek()
            },
            modifier = Modifier.weight(1F).semantics { role = Role.RadioButton },
            shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
        ) {
            AnimatedVisibility(
                visible = selected is TimeWindow.Week
            ) {
                MaterialSymbols(
                    modifier = Modifier.size(ToggleButtonDefaults.IconSize),
                    name = MaterialSymbols.CHECK,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(ToggleButtonDefaults.IconSpacing))
            }
            Text(
                text = stringResource(Res.string.home_week),
                maxLines = 1
            )
        }
    }
}