package dev.datlag.mimasu.ui.custom.login

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.firebase.auth.provider.email.EmailAuthParams
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.rememberGoogleAuthParams
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.custom.EMailTextField
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.custom.login.components.LoginAgreement
import dev.datlag.mimasu.ui.custom.login.components.LoginAppImage
import dev.datlag.mimasu.ui.custom.login.components.LoginPasswordCriteriaSection
import dev.datlag.mimasu.ui.custom.login.components.LoginResult
import dev.datlag.mimasu.ui.custom.login.components.LoginSignInButton
import dev.datlag.mimasu.ui.custom.login.components.LoginSocialProvider
import dev.datlag.mimasu.ui.custom.login.components.LoginSocialProviderDivider
import dev.datlag.mimasu.ui.login_email
import dev.datlag.mimasu.ui.login_password
import dev.datlag.mimasu.ui.viewmodel.LoginViewModel
import dev.datlag.mimasu.ui.viewmodel.loginViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.withMainContext
import dev.datlag.tooling.compose.platform.PlatformIconButton
import dev.datlag.tooling.compose.platform.localTextStyle
import dev.datlag.tooling.compose.platform.rememberIsTv
import io.tolgee.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(
    appImage: Painter,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = Platform.localTextStyle(),
    onSuccess: () -> Unit
) {
    val loginViewModel = loginViewModel()
    val loginResult by loginViewModel.loginResult.collectAsState()

    val emailValue by loginViewModel.email.collectAsState()
    val emailHasError by loginViewModel.emailHasError.collectAsState(false)
    val emailValid = remember(emailValue, emailHasError) {
        emailValue.isNotBlank() && !emailHasError
    }
    val emailReadonly by loginViewModel.emailReadonly.collectAsState()
    val emailInteractionSource = remember { MutableInteractionSource() }
    val typingEmail by emailInteractionSource.collectIsFocusedAsState()

    val passwordValue by loginViewModel.password.collectAsState()
    val passwordErrorState by loginViewModel.passwordErrorState.collectAsState(null)
    val passwordHasError = remember(passwordErrorState) {
        passwordErrorState?.hasError == true
    }
    val passwordValid = remember(passwordValue, passwordHasError) {
        passwordValue.isNotBlank() && !passwordHasError
    }
    val passwordInteractionSource = remember { MutableInteractionSource() }
    val typingPassword by passwordInteractionSource.collectIsFocusedAsState()
    val passwordResetCode by loginViewModel.passwordResetCode.collectAsState()
    val passwordResetUi by loginViewModel.passwordResetUi.collectAsState()

    val focusManager = LocalFocusManager.current

    BackHandler(enabled = !Platform.rememberIsTv(anyOS = true) && (typingEmail || typingPassword)) {
        focusManager.clearFocus(true)
    }

    Box(
        modifier = modifier
    ) {
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
                        imagePainter = appImage,
                        imageModifier = Modifier.size(200.dp).clip(CircleShape),
                        riveModifier = Modifier.fillMaxWidth(),
                        typingEmail = typingEmail && !typingPassword,
                        typingPassword = typingPassword && !typingEmail
                    )
                    EMailTextField(
                        modifier = Modifier.fillParentMaxWidth(),
                        value = emailValue,
                        onValueChange = {
                            loginViewModel.updateEmail(it)
                        },
                        label = {
                            Text(text = uiStringRes(UiRes.string.login_email))
                        },
                        textStyle = textStyle,
                        isError = emailHasError || loginResult is LoginViewModel.LoginResult.Disposable,
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
                        loginViewModel.updatePassword(it)
                    },
                    leadingIcon = {
                        MaterialSymbols(
                            name = MaterialSymbols.PASSWORD,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = uiStringRes(UiRes.string.login_password))
                    },
                    textStyle = textStyle,
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
                        loginViewModel.resetPassword(emailValue)
                    }
                )
            }
            item {
                LoginSignInButton(
                    enabled = emailValid && passwordValid,
                    passwordReset = passwordResetUi,
                    modifier = Modifier.fillParentMaxWidth(),
                    onSignIn = {
                        loginViewModel.emailSignIn(
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
                        loginViewModel.changePassword(
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
                        hasGitHubProvider = loginViewModel.hasGitHubProvider,
                        hasGoogleProvider = loginViewModel.hasGoogleProvider,
                        modifier = Modifier.fillParentMaxWidth().padding(vertical = 8.dp)
                    )
                }
                item {
                    val googleSignInParams = rememberGoogleAuthParams()

                    LoginSocialProvider(
                        hasGitHubProvider = loginViewModel.hasGitHubProvider,
                        hasGoogleProvider = loginViewModel.hasGoogleProvider,
                        modifier = Modifier.fillParentMaxWidth(),
                        onGitHubClicked = { params ->
                            loginViewModel.githubSignIn(params) {
                                withMainContext {
                                    onSuccess()
                                }
                            }
                        },
                        onGoogleClicked = {
                            loginViewModel.googleSignIn(googleSignInParams) {
                                withMainContext {
                                    onSuccess()
                                }
                            }
                        }
                    )
                }
                item {
                    LoginResult(
                        failure = loginResult,
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
        PlatformIconButton(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            onClick = {
                onSuccess()
            }
        ) {
            MaterialSymbols(
                name = MaterialSymbols.CLOSE,
                contentDescription = null
            )
        }
    }
}