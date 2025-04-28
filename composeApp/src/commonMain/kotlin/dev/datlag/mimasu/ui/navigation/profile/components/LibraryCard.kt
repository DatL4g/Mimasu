package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import dev.datlag.mimasu.core.Constants
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography

@Composable
fun LibraryCard(
    library: Library,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val website = remember(library) {
        library.website?.ifBlank { null } ?: library.scm?.url?.ifBlank { null }
    }

    ElevatedCard(
        onClick = {
            if (!website.isNullOrBlank()) {
                uriHandler.openUri(website)
            }
        },
        modifier = modifier,
        enabled = !website.isNullOrBlank()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
        ) {
            val owner = remember(library) {
                library.organization?.name?.ifBlank { null } ?: library.developers.mapNotNull { it.name?.ifBlank { null } }.joinToString()
            }
            val name = remember(library) {
                if (library.name.equals(library.artifactId, ignoreCase = true) || library.artifactId.startsWith(library.name, ignoreCase = true)) {
                    library.name.split(':').last()
                } else {
                    library.name
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = name,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    fontWeight = FontWeight.Medium
                )
                library.artifactVersion?.let {
                    Text(
                        text = it,
                        style = Platform.typography().bodySmall
                    )
                }
            }
            Text(
                text = owner,
                style = Platform.typography().bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = true
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                library.licenses.forEach { lic ->
                    val url = remember(lic) { lic.url?.ifBlank { null } ?: lic.spdxId?.ifBlank { null }?.let { "${Constants.SPDX_LICENSE_BASE}$it" } }

                    SuggestionChip(
                        onClick = {
                            if (!url.isNullOrBlank()) {
                                uriHandler.openUri(url)
                            }
                        },
                        enabled = !url.isNullOrBlank(),
                        label = {
                            Text(
                                text = lic.name,
                                softWrap = true,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Platform.colorScheme().tertiary,
                            labelColor = Platform.colorScheme().onTertiary
                        ),
                        border = null
                    )
                }
            }
        }
    }
}