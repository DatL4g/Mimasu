package dev.datlag.mimasu.ui.navigation.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle

@Composable
fun LoginScreen(component: LoginComponent) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            val email by component.emailInput.collectAsStateWithLifecycle()

            OutlinedTextField(
                modifier = Modifier.fillParentMaxWidth(0.7F),
                value = email,
                onValueChange = {
                    component.updateEmail(it)
                },
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
                    keyboardType = KeyboardType.Email,
                ),
                shape = CircleShape
            )
        }
        item {
            val password by component.passwordInput.collectAsStateWithLifecycle()

            OutlinedTextField(
                modifier = Modifier.fillParentMaxWidth(0.7F),
                value = password,
                onValueChange = {
                    component.updatePassword(it)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Password,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Password")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Password,
                ),
                shape = CircleShape
            )
        }
        item {
            Button(
                modifier = Modifier.padding(top = 16.dp).fillParentMaxWidth(0.7F),
                onClick = {

                }
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
                modifier = Modifier.fillParentMaxWidth(0.7F),
                onClick = {

                }
            ) {
                Text("Forgot Password")
            }
        }
        item {
            Button(
                modifier = Modifier.padding(top = 16.dp).fillParentMaxWidth(0.7F),
                onClick = {

                }
            ) {
                Text(text = "Google")
            }
        }
        item {
            Button(
                modifier = Modifier.fillParentMaxWidth(0.7F),
                onClick = {

                }
            ) {
                Text(text = "GitHub")
            }
        }
    }
}