package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_open_source_licenses
import dev.datlag.mimasu.composeapp.generated.resources.profile_open_source_licenses_text
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.onClick
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesSection(
    modifier: Modifier = Modifier
) {
    val libs by rememberLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }
    val libraries = remember(libs) {
        libs?.libraries.orEmpty().toImmutableSet().filter { it.openSource }.toImmutableList()
    }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = {
                showDialog = false
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        modifier = Modifier.fillParentMaxWidth(),
                        text = stringResource(Res.string.profile_open_source_licenses),
                        style = Platform.typography().headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Text(
                        modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp),
                        text = stringResource(Res.string.profile_open_source_licenses_text),
                        textAlign = TextAlign.Center
                    )
                }
                items(libraries, key = { it.uniqueId }) {
                    LibraryCard(it)
                }
            }
        }
    }

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = ButtonDefaults.MinHeight, minWidth = ButtonDefaults.MinWidth)
            .clip(Platform.shapes().medium)
            .onClick(enabled = libraries.isNotEmpty()) {
                showDialog = !showDialog
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MaterialSymbols(
            name = MaterialSymbols.CONTRACT,
            contentDescription = null
        )
        Text(
            text = stringResource(Res.string.profile_open_source_licenses),
            maxLines = 2,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
    }
}