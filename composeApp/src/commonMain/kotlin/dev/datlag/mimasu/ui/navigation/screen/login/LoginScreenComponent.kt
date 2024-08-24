package dev.datlag.mimasu.ui.navigation.screen.login

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.kodein.di.DI

class LoginScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
) : LoginComponent, ComponentContext by componentContext {

    private val _emailInput = MutableStateFlow("")
    override val emailInput: StateFlow<String> = _emailInput.asStateFlow()

    private val _passwordInput = MutableStateFlow("")
    override val passwordInput: StateFlow<String> = _passwordInput.asStateFlow()

    @Composable
    override fun renderCommon() {
        onRender {
            LoginScreen(this)
        }
    }

    override fun updateEmail(input: String) {
        _emailInput.update { input }
    }

    override fun updatePassword(input: String) {
        _passwordInput.update { input }
    }
}