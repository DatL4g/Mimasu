package dev.datlag.mimasu.ui.custom.login.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.ui.UiRes
import dev.datlag.mimasu.ui.common.uiStringRes
import dev.datlag.mimasu.ui.login_forgot_password
import dev.datlag.mimasu.ui.login_password_criteria_length_minimum
import dev.datlag.mimasu.ui.login_password_criteria_lowercase
import dev.datlag.mimasu.ui.login_password_criteria_number
import dev.datlag.mimasu.ui.login_password_criteria_special
import dev.datlag.mimasu.ui.login_password_criteria_uppercase
import dev.datlag.mimasu.ui.viewmodel.LoginViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.rememberIsTv
import io.tolgee.stringResource

@Composable
internal fun LoginPasswordCriteriaSection(
    emailValid: Boolean,
    criteriaVisible: Boolean,
    passwordErrorState: LoginViewModel.PasswordErrorState?,
    modifier: Modifier = Modifier,
    onPasswordReset: () -> Unit
) {
    var resetPasswordSent by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
    ) {
        AnimatedVisibility(
            modifier = Modifier.weight(1F),
            visible = criteriaVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                LoginPasswordCriteria(
                    fulfilled = passwordErrorState?.hasLowercaseLetter == true,
                    text = UiRes.string.login_password_criteria_lowercase
                )
                LoginPasswordCriteria(
                    fulfilled = passwordErrorState?.hasUppercaseLetter == true,
                    text = UiRes.string.login_password_criteria_uppercase
                )
                LoginPasswordCriteria(
                    fulfilled = passwordErrorState?.hasNumber == true,
                    text = UiRes.string.login_password_criteria_number
                )
                LoginPasswordCriteria(
                    fulfilled = passwordErrorState?.hasSpecialChar == true,
                    text = UiRes.string.login_password_criteria_special
                )
                LoginPasswordCriteria(
                    fulfilled = passwordErrorState?.isLongEnough == true,
                    text = UiRes.string.login_password_criteria_length_minimum
                )
            }
        }
        if (!Platform.rememberIsTv() && !resetPasswordSent) {
            TextButton(
                modifier = Modifier,
                onClick = {
                    onPasswordReset()
                    resetPasswordSent = true
                },
                enabled = emailValid && !resetPasswordSent
            ) {
                Text(text = uiStringRes(UiRes.string.login_forgot_password))
            }
        }
    }
}