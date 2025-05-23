package dev.datlag.mimasu.ui.navigation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.ui.custom.GitHubButton
import dev.datlag.mimasu.ui.custom.GitHubIconButton
import dev.datlag.mimasu.ui.custom.GoogleButton
import dev.datlag.mimasu.ui.custom.GoogleIconButton
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.login.components.LoginAppImage
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.github
import dev.datlag.mimasu.composeapp.generated.resources.google
import dev.datlag.mimasu.composeapp.generated.resources.login_agreement
import dev.datlag.mimasu.composeapp.generated.resources.login_email
import dev.datlag.mimasu.composeapp.generated.resources.login_failure
import dev.datlag.mimasu.composeapp.generated.resources.login_forgot_password
import dev.datlag.mimasu.composeapp.generated.resources.login_or_login_with
import dev.datlag.mimasu.composeapp.generated.resources.login_password
import dev.datlag.mimasu.composeapp.generated.resources.login_password_criteria_length_minimum
import dev.datlag.mimasu.composeapp.generated.resources.login_password_criteria_lowercase
import dev.datlag.mimasu.composeapp.generated.resources.login_password_criteria_number
import dev.datlag.mimasu.composeapp.generated.resources.login_password_criteria_special
import dev.datlag.mimasu.composeapp.generated.resources.login_password_criteria_uppercase
import dev.datlag.mimasu.composeapp.generated.resources.login_password_reset_email_nothing_receiving
import dev.datlag.mimasu.composeapp.generated.resources.login_password_reset_email_sent
import dev.datlag.mimasu.composeapp.generated.resources.login_privacy_policy
import dev.datlag.mimasu.composeapp.generated.resources.login_sign_in
import dev.datlag.mimasu.composeapp.generated.resources.login_terms_of_service
import dev.datlag.mimasu.firebase.auth.provider.email.EmailAuthParams
import dev.datlag.mimasu.ui.navigation.login.components.LoginAgreement
import dev.datlag.mimasu.ui.navigation.login.components.LoginPasswordCriteria
import dev.datlag.mimasu.ui.navigation.login.components.LoginPasswordCriteriaSection
import dev.datlag.mimasu.ui.navigation.login.components.LoginResult
import dev.datlag.mimasu.ui.navigation.login.components.LoginSignInButton
import dev.datlag.mimasu.ui.navigation.login.components.LoginSocialProvider
import dev.datlag.mimasu.ui.navigation.login.components.LoginSocialProviderDivider
import dev.datlag.mimasu.ui.viewmodel.AccountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.PlatformButton
import dev.datlag.tooling.compose.platform.PlatformText
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.rememberIsTv
import dev.datlag.tooling.compose.withMainContext
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min
import kotlin.onSuccess

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(onSuccess: () -> Unit) {
    val accountViewModel = accountViewModel()

    val emailValue by accountViewModel.email.collectAsStateWithLifecycle()
    val emailHasError by accountViewModel.emailHasError.collectAsStateWithLifecycle(false)
    val emailValid = remember(emailValue, emailHasError) {
        emailValue.isNotBlank() && !emailHasError
    }
    val emailReadonly by accountViewModel.emailReadonly.collectAsStateWithLifecycle()
    val emailInteractionSource = remember { MutableInteractionSource() }
    val typingEmail by emailInteractionSource.collectIsFocusedAsState()

    val passwordValue by accountViewModel.password.collectAsStateWithLifecycle()
    val passwordErrorState by accountViewModel.passwordErrorState.collectAsStateWithLifecycle(null)
    val passwordHasError = remember(passwordErrorState) {
        passwordErrorState?.hasError == true
    }
    val passwordValid = remember(passwordValue, passwordHasError) {
        passwordValue.isNotBlank() && !passwordHasError
    }
    val passwordInteractionSource = remember { MutableInteractionSource() }
    val typingPassword by passwordInteractionSource.collectIsFocusedAsState()
    val passwordResetCode by accountViewModel.passwordResetCode.collectAsStateWithLifecycle()
    val passwordResetUi by accountViewModel.passwordResetUi.collectAsStateWithLifecycle()

    LaunchedEffect(passwordResetCode) {
        accountViewModel.verifyPasswordResetCode(passwordResetCode)
    }

    val focusManager = LocalFocusManager.current

    BackHandler(enabled = typingEmail || typingPassword) {
        focusManager.clearFocus(true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillParentMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginAppImage(
                    imageModifier = Modifier.size(200.dp).clip(CircleShape),
                    riveModifier = Modifier.fillMaxWidth(),
                    typingEmail = typingEmail && !typingPassword,
                    typingPassword = typingPassword && !typingEmail
                )
                OutlinedTextField(
                    modifier = Modifier.fillParentMaxWidth().semantics {
                        contentType = ContentType.EmailAddress
                        contentDataType = ContentDataType.Text
                    },
                    value = emailValue,
                    onValueChange = {
                        accountViewModel.updateEmail(it)
                    },
                    leadingIcon = {
                        MaterialSymbols(
                            name = MaterialSymbols.MAIL,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = stringResource(Res.string.login_email))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    singleLine = true,
                    isError = emailHasError,
                    interactionSource = emailInteractionSource
                )
            }
        }
        item {
            var showPassword by remember { mutableStateOf(false) }

            OutlinedTextField(
                modifier = Modifier.fillParentMaxWidth().semantics {
                    contentType = ContentType.Password // + ContentType.NewPassword // not supported yet?
                    contentDataType = ContentDataType.Text
                },
                value = passwordValue,
                onValueChange = {
                    accountViewModel.updatePassword(it)
                },
                leadingIcon = {
                    MaterialSymbols(
                        name = MaterialSymbols.PASSWORD,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = stringResource(Res.string.login_password))
                },
                trailingIcon = if (passwordValue.isBlank()) null else {
                    {
                        IconButton(
                            onClick = {
                                showPassword = !showPassword
                            }
                        ) {
                            MaterialSymbols(
                                name = if (showPassword) {
                                    MaterialSymbols.VISIBILITY_OFF
                                } else {
                                    MaterialSymbols.VISIBILITY
                                },
                                contentDescription = null
                            )
                        }
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
                maxLines = 1,
                singleLine = true,
                isError = passwordHasError,
                readOnly = emailReadonly,
                interactionSource = passwordInteractionSource
            )
        }
        item {
            LoginPasswordCriteriaSection(
                emailValid = emailValid,
                criteriaVisible = passwordValue.isNotEmpty(),
                passwordErrorState = passwordErrorState,
                modifier = Modifier.fillParentMaxWidth(),
                onPasswordReset = {
                    accountViewModel.resetPassword(emailValue)
                }
            )
        }
        item {
            LoginSignInButton(
                enabled = emailValid && passwordValid,
                passwordReset = passwordResetUi,
                modifier = Modifier.fillParentMaxWidth(),
                onSignIn = {
                    accountViewModel.emailSignIn(
                        params = EmailAuthParams(
                            email = emailValue,
                            password = passwordValue
                        ),
                        onSuccess = {
                            withMainContext {
                                onSuccess()
                            }
                        }
                    )
                },
                onPasswordReset = {
                    accountViewModel.changePassword(
                        code = passwordResetCode,
                        email = emailValue,
                        newPassword = passwordValue,
                        onSuccess = {
                            withMainContext {
                                onSuccess()
                            }
                        }
                    )
                }
            )
        }
        if (!passwordResetUi) {
            item {
                LoginSocialProviderDivider(
                    hasGitHubProvider = accountViewModel.hasGitHubProvider,
                    hasGoogleProvider = accountViewModel.hasGoogleProvider,
                    modifier = Modifier.fillParentMaxWidth().padding(vertical = 8.dp)
                )
            }
            item {
                LoginSocialProvider(
                    hasGitHubProvider = accountViewModel.hasGitHubProvider,
                    hasGoogleProvider = accountViewModel.hasGoogleProvider,
                    modifier = Modifier.fillParentMaxWidth(),
                    onGitHubClicked = { params ->
                        accountViewModel.githubSignIn(params) {
                            withMainContext {
                                onSuccess()
                            }
                        }
                    },
                    onGoogleClicked = {
                        accountViewModel.googleSignIn {
                            withMainContext {
                                onSuccess()
                            }
                        }
                    }
                )
            }
            item {
                val loginResult by accountViewModel.loginResult.collectAsStateWithLifecycle()

                LoginResult(
                    failure = loginResult == false,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }
        item {
            LoginAgreement(
                modifier = Modifier.fillParentMaxWidth().padding(vertical = 16.dp)
            )
        }
    }
}