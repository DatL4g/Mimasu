package dev.datlag.mimasu.ui.navigation.screen.login

import dev.datlag.mimasu.ui.navigation.Component
import kotlinx.coroutines.flow.StateFlow

interface LoginComponent : Component {
    val emailInput: StateFlow<String>
    val passwordInput: StateFlow<String>

    fun updateEmail(input: String)
    fun updatePassword(input: String)
}