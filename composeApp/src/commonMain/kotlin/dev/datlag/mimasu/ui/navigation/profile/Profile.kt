package dev.datlag.mimasu.ui.navigation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dev.datlag.mimasu.common.rememberGitHubAuthParams
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.github
import dev.datlag.mimasu.composeapp.generated.resources.google
import dev.datlag.mimasu.composeapp.generated.resources.home_people
import dev.datlag.mimasu.composeapp.generated.resources.profile_about
import dev.datlag.mimasu.composeapp.generated.resources.profile_about_text
import dev.datlag.mimasu.composeapp.generated.resources.profile_connect
import dev.datlag.mimasu.composeapp.generated.resources.profile_connected
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_header
import dev.datlag.mimasu.composeapp.generated.resources.profile_extension_text
import dev.datlag.mimasu.composeapp.generated.resources.profile_open_source
import dev.datlag.mimasu.composeapp.generated.resources.profile_open_source_text
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out_cancel
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out_text
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out_yes
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.profile.components.AboutHeader
import dev.datlag.mimasu.ui.navigation.profile.components.LicensesSection
import dev.datlag.mimasu.ui.navigation.profile.components.OwnerSection
import dev.datlag.mimasu.ui.navigation.profile.components.RepositorySection
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun Profile(
    onLogout: () -> Unit,
) {
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsStateWithLifecycle()

    var showSignOutDialog by remember { mutableStateOf(false) }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = {
                showSignOutDialog = false
            },
            icon = {
                MaterialSymbols(
                    name = MaterialSymbols.LOGOUT,
                    contentDescription = null
                )
            },
            title = {
                Text(text = stringResource(Res.string.profile_sign_out))
            },
            text = {
                Text(text = stringResource(Res.string.profile_sign_out_text))
            },
            dismissButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        accountViewModel.signOut()?.invokeOnCompletion {
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Platform.colorScheme().error,
                        contentColor = Platform.colorScheme().onError
                    )
                ) {
                    Text(text = stringResource(Res.string.profile_sign_out_yes))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                    }
                ) {
                    Text(text = stringResource(Res.string.profile_sign_out_cancel))
                }
            },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.statusBars.asPaddingValues()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    model = user?.profilePictures?.firstOrNull(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    error = rememberNestedImagePainter(
                        models = user?.profilePictures.orEmpty().drop(1),
                        contentScale = ContentScale.Crop
                    )
                )
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    visible = user != null
                ) {
                    IconButton(
                        onClick = {
                            showSignOutDialog = true
                        }
                    ) {
                        MaterialSymbols(
                            name = MaterialSymbols.LOGOUT,
                            contentDescription = null,
                            tint = Platform.colorScheme().error
                        )
                    }
                }
            }
        }
        item {
            Text(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                text = user?.name ?: "",
                style = Platform.typography().headlineMedium,
                fontWeight = FontWeight.SemiBold,
                softWrap = true
            )
        }
        if (accountViewModel.hasGoogleProvider) {
            item {
                Row(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = MaterialSymbols.GoogleGLogo,
                        contentDescription = stringResource(Res.string.google),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(Res.string.google)
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Button(
                        onClick = {
                            accountViewModel.googleLink()
                        },
                        enabled = user?.linkedGoogle != true
                    ) {
                        Text(
                            text = if (user?.linkedGoogle == true) {
                                stringResource(Res.string.profile_connected)
                            } else {
                                stringResource(Res.string.profile_connect)
                            }
                        )
                    }
                }
            }
        }
        if (accountViewModel.hasGitHubProvider) {
            item {
                Row(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val githubAuthParams = rememberGitHubAuthParams()

                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = MaterialSymbols.Github,
                        contentDescription = stringResource(Res.string.github)
                    )
                    Text(
                        text = stringResource(Res.string.github)
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Button(
                        onClick = {
                            githubAuthParams?.let {
                                accountViewModel.githubLink(it)
                            }
                        },
                        enabled = user?.github?.linked != true && githubAuthParams != null
                    ) {
                        Text(
                            text = if (user?.github?.linked == true) {
                                stringResource(Res.string.profile_connected)
                            } else {
                                stringResource(Res.string.profile_connect)
                            }
                        )
                    }
                }
            }
        }
        item {
            AboutHeader(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            )
        }
        item {
            Text(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.profile_about_text)
            )
        }
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.profile_open_source),
                style = Platform.typography().titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
        item {
            Text(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.profile_open_source_text)
            )
        }
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.profile_extension_header),
                style = Platform.typography().titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
        item {
            Text(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = stringResource(Res.string.profile_extension_text)
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