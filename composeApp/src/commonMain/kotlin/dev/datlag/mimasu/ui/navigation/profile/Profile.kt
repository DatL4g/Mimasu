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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
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
import dev.datlag.mimasu.composeapp.generated.resources.profile_connect
import dev.datlag.mimasu.composeapp.generated.resources.profile_connected
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.typography
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun Profile(
    onLogout: () -> Unit
) {
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsStateWithLifecycle()

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
                            accountViewModel.signOut()?.invokeOnCompletion {
                                onLogout()
                            }
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
            val info by remember(user) {
                user?.info ?: flowOf(null)
            }.collectAsStateWithLifecycle(null)

            Text("Adult: ${info?.adult}")
        }
    }
}