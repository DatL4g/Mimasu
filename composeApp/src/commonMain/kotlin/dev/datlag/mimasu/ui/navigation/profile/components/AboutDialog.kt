package dev.datlag.mimasu.ui.navigation.profile.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_about
import dev.datlag.mimasu.composeapp.generated.resources.profile_about_text
import dev.datlag.mimasu.composeapp.generated.resources.profile_open_source
import dev.datlag.mimasu.composeapp.generated.resources.profile_open_source_text
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    text = stringResource(Res.string.profile_about),
                    style = Platform.typography().headlineSmall,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = stringResource(Res.string.profile_about_text),
                    textAlign = TextAlign.Center
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                        .padding(horizontal = 16.dp),
                    text = stringResource(Res.string.profile_open_source),
                    style = Platform.typography().titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                        .padding(horizontal = 16.dp),
                    text = stringResource(Res.string.profile_open_source_text),
                    textAlign = TextAlign.Center
                )
            }
            item {
                HomepageSection(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                LicensesSection(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                OwnerSection(modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                RepositorySection(modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}