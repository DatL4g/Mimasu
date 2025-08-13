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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile_connect
import dev.datlag.mimasu.composeapp.generated.resources.profile_connected
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out_cancel
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out_text
import dev.datlag.mimasu.composeapp.generated.resources.profile_sign_out_yes
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.rememberGitHubAuthParams
import dev.datlag.mimasu.ui.common.rememberGoogleAuthParams
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.github
import dev.datlag.mimasu.ui.google
import dev.datlag.mimasu.ui.navigation.profile.components.AboutDialog
import dev.datlag.mimasu.ui.navigation.profile.components.ExtensionSection
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.mimasu.ui.viewmodel.loginViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography
import io.tolgee.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Profile(
    onLogout: () -> Unit,
) {
    val accountViewModel = accountViewModel()
    val loginViewModel = loginViewModel()
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
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        loginViewModel.signOut()?.invokeOnCompletion {
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Platform.colorScheme().error
                    ),
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(text = stringResource(Res.string.profile_sign_out_yes))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                    },
                    shapes = ButtonDefaults.shapes()
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
                var aboutDialog by remember { mutableStateOf(false) }
                var fallback by remember { mutableStateOf(false) }

                if (aboutDialog) {
                    AboutDialog(
                        onDismiss = {
                            aboutDialog = false
                        }
                    )
                }

                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        aboutDialog = !aboutDialog
                    }
                ) {
                    MaterialSymbols(
                        name = MaterialSymbols.INFO,
                        contentDescription = null
                    )
                }
                if (fallback) {
                    MaterialSymbols(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        name = MaterialSymbols.ACCOUNT_CIRCLE,
                        contentDescription = null,
                        filled = true
                    )
                } else {
                    AsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        model = user?.profilePictures?.firstOrNull(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        error = rememberNestedImagePainter(
                            models = user?.profilePictures.orEmpty().drop(1),
                            contentScale = ContentScale.Crop,
                            onError = {
                                fallback = true
                            },
                            onSuccess = {
                                fallback = false
                            }
                        ),
                        onSuccess = {
                            fallback = false
                        }
                    )
                }
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
        if (loginViewModel.hasGoogleProvider) {
            item {
                Row(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val linked = user?.linkedGoogle == true
                    val googleSignIn = rememberGoogleAuthParams()

                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = MaterialSymbols.GoogleGLogo,
                        contentDescription = uiStringRes(UiRes.string.google),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = uiStringRes(UiRes.string.google)
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Button(
                        onClick = {
                            loginViewModel.googleLink(googleSignIn)
                        },
                        enabled = !linked,
                        shapes = ButtonDefaults.shapes()
                    ) {
                        MaterialSymbols(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = if (linked) MaterialSymbols.LINK else MaterialSymbols.LINK_OFF,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = if (linked) {
                                stringResource(Res.string.profile_connected)
                            } else {
                                stringResource(Res.string.profile_connect)
                            }
                        )
                    }
                }
            }
        }
        if (loginViewModel.hasGitHubProvider) {
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
                    val linked = user?.github?.linked == true

                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = MaterialSymbols.Github,
                        contentDescription = uiStringRes(UiRes.string.github)
                    )
                    Text(
                        text = uiStringRes(UiRes.string.github)
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Button(
                        onClick = {
                            githubAuthParams?.let {
                                loginViewModel.githubLink(it)
                            }
                        },
                        enabled = !linked && githubAuthParams != null,
                        shapes = ButtonDefaults.shapes()
                    ) {
                        MaterialSymbols(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = if (linked) MaterialSymbols.LINK else MaterialSymbols.LINK_OFF,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = if (linked) {
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
            ExtensionSection(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}