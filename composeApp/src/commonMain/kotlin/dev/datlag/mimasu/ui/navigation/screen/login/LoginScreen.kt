package dev.datlag.mimasu.ui.navigation.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.githubAuthParams
import dev.datlag.mimasu.tv.common.autoFill
import dev.datlag.tooling.Platform
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(component: LoginComponent) {
    Column(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeContent).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        val emailPasswordEnabled by component.emailPasswordEnabled.collectAsStateWithLifecycle()

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1F),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                val email by component.emailInput.collectAsStateWithLifecycle()

                OutlinedTextField(
                    modifier = Modifier.fillParentMaxWidth().autoFill(
                        types = persistentListOf(
                            AutofillType.EmailAddress
                        ),
                        onFill = {
                            component.updateEmail(it)
                        }
                    ),
                    value = email,
                    onValueChange = {
                        component.updateEmail(it)
                    },
                    enabled = emailPasswordEnabled,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Email,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "E-Mail")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Email
                    ),
                    shape = CircleShape,
                    maxLines = 1,
                    singleLine = true
                )
            }
            item {
                val password by component.passwordInput.collectAsStateWithLifecycle()

                OutlinedTextField(
                    modifier = Modifier.fillParentMaxWidth().autoFill(
                        types = persistentListOf(
                            AutofillType.Password,
                            AutofillType.NewPassword
                        ),
                        onFill = {
                            component.updatePassword(it)
                        }
                    ),
                    value = password,
                    onValueChange = {
                        component.updatePassword(it)
                    },
                    enabled = emailPasswordEnabled,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Password,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "Password")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Password,
                    ),
                    shape = CircleShape,
                    maxLines = 1,
                    singleLine = true
                )
            }
            item {
                Button(
                    modifier = Modifier.padding(top = 16.dp).fillParentMaxWidth(),
                    onClick = {

                    },
                    enabled = emailPasswordEnabled
                ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = Icons.AutoMirrored.Rounded.Login,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Sign In")
                }
            }
            item {
                TextButton(
                    modifier = Modifier.fillParentMaxWidth(),
                    onClick = {

                    },
                    enabled = emailPasswordEnabled
                ) {
                    Text("Forgot Password")
                }
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(top = 32.dp).fillParentMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    maxItemsInEachRow = 2
                ) {
                    if (component.googleAuthProvider != null) {
                        val googleEnabled by component.googleEnabled.collectAsStateWithLifecycle()

                        Button(
                            modifier = Modifier.weight(1F),
                            onClick = {
                                component.googleLogin()
                            },
                            enabled = googleEnabled
                        ) {
                            Text(text = "Google")
                        }
                    }
                    if (component.githubAuthProvider != null) {
                        val authParams = Platform.githubAuthParams()
                        val githubEnabled by component.githubEnabled.collectAsStateWithLifecycle()

                        Button(
                            modifier = Modifier.weight(1F),
                            onClick = {
                                component.githubLogin(authParams)
                            },
                            enabled = githubEnabled
                        ) {
                            Text(text = "GitHub")
                        }
                    }
                }
            }
        }
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

            }
        ) {
            Text("Skip for now")
        }
    }

}