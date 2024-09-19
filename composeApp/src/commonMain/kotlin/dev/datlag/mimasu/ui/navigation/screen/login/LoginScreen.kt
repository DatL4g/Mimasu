package dev.datlag.mimasu.ui.navigation.screen.login

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.PlatformImeOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.githubAuthParams
import dev.datlag.mimasu.tv.common.autoFill
import dev.datlag.mimasu.ui.custom.GitHubButton
import dev.datlag.mimasu.ui.custom.GitHubIconButton
import dev.datlag.mimasu.ui.custom.GoogleButton
import dev.datlag.mimasu.ui.custom.GoogleIconButton
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
        val emailPasswordFailure by component.emailPasswordFailure.collectAsStateWithLifecycle()
        val (emailFocus, passwordFocus) = remember {
            FocusRequester.createRefs()
        }
        val email by component.emailInput.collectAsStateWithLifecycle()
        val password by component.passwordInput.collectAsStateWithLifecycle()
        val emailValid by component.emailValid.collectAsStateWithLifecycle()
        val passwordValid by component.passwordValid.collectAsStateWithLifecycle()
        val loginClickable by component.loginClickable.collectAsStateWithLifecycle()
        val sendClickable by component.sendClickable.collectAsStateWithLifecycle()
        var showPassword by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.weight(1F))
        AnimatedVisibility(
            visible = emailPasswordFailure
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "E-Mail or Password is wrong."
            )
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().autoFill(
                types = persistentListOf(
                    AutofillType.EmailAddress
                ),
                onFill = {
                    component.updateEmail(it)
                }
            ).focusRequester(emailFocus),
            value = email,
            onValueChange = {
                component.updateEmail(it)
            },
            isError = emailPasswordFailure || !emailValid,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "E-Mail")
            },
            keyboardActions = KeyboardActions(
                onNext = {
                    passwordFocus.requestFocus()
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = CircleShape,
            maxLines = 1,
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().autoFill(
                types = persistentListOf(
                    AutofillType.Password,
                    AutofillType.NewPassword
                ),
                onFill = {
                    component.updatePassword(it)
                }
            ).focusRequester(passwordFocus),
            value = password,
            onValueChange = {
                component.updatePassword(it)
            },
            isError = emailPasswordFailure || !passwordValid,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Password,
                    contentDescription = null
                )
            },
            label = {
                Text(text = "Password")
            },
            trailingIcon = if (password.isBlank()) null else {
                {
                    IconButton(
                        onClick = {
                            showPassword = !showPassword
                        }
                    ) {
                        Icon(
                            imageVector = if (showPassword) {
                                Icons.Rounded.VisibilityOff
                            } else {
                                Icons.Rounded.Visibility
                            },
                            contentDescription = null
                        )
                    }
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onGo = {
                    component.emailLogin()
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Go
            ),
            shape = CircleShape,
            maxLines = 1,
            singleLine = true
        )

        Button(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            onClick = {
                component.emailLogin()
            },
            enabled = loginClickable
        ) {
            Icon(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                imageVector = Icons.AutoMirrored.Rounded.Login,
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Sign In")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

            },
            enabled = sendClickable
        ) {
            Text("Send Login Link")
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {

            GoogleIconButton(
                modifier = Modifier.size(56.dp),
                onClick = {
                    component.googleLogin()
                },
                iconSize = 24.dp,
            )

            GitHubIconButton(
                modifier = Modifier.size(56.dp),
                onClick = {
                    component.githubLogin(it)
                },
                iconSize = 24.dp,
            )
        }

        Spacer(modifier = Modifier.weight(1F))
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                component.skipLogin()
            }
        ) {
            Text("Skip for now")
        }
    }

}