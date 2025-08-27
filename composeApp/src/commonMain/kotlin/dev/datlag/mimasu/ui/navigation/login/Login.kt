package dev.datlag.mimasu.ui.navigation.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.custom.login.Login
import dev.datlag.mimasu.ui.viewmodel.loginViewModel
import dev.datlag.tooling.compose.LaunchedVirtualIO

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(onSuccess: () -> Unit) {
    val loginViewModel = loginViewModel()
    val passwordResetCode by loginViewModel.passwordResetCode.collectAsStateWithLifecycle()

    LaunchedVirtualIO(passwordResetCode) {
        loginViewModel.verifyPasswordResetCode(passwordResetCode)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Login(
            appImage = rememberAppImage(),
            modifier = Modifier.padding(padding).fillMaxSize(),
            onSuccess = onSuccess
        )
    }
}

@Composable
expect fun rememberAppImage(): Painter