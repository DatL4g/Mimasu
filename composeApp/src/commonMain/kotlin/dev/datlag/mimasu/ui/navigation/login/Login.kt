package dev.datlag.mimasu.ui.navigation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
import dev.datlag.mimasu.ui.custom.GitHubButton
import dev.datlag.mimasu.ui.custom.GitHubIconButton
import dev.datlag.mimasu.ui.custom.GoogleButton
import dev.datlag.mimasu.ui.custom.GoogleIconButton
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.viewmodel.accountViewModel

@Composable
fun Login() {
    val accountViewModel = accountViewModel()
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillParentMaxHeight(0.3F).fillParentMaxWidth()
            ) {  }
        }
        item {
            OutlinedTextField(
                modifier = Modifier.fillParentMaxWidth().semantics {
                    contentType = ContentType.EmailAddress
                    contentDataType = ContentDataType.Text
                },
                value = emailValue,
                onValueChange = {
                    emailValue = it
                },
                leadingIcon = {
                    MaterialSymbols(
                        name = MaterialSymbols.MAIL,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "E-Mail")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                singleLine = true
            )
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
                    passwordValue = it
                },
                leadingIcon = {
                    MaterialSymbols(
                        name = MaterialSymbols.PASSWORD,
                        contentDescription = null
                    )
                },
                label = {
                    Text(text = "Password")
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
                singleLine = true
            )
        }
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier,
                    onClick = {

                    },
                    enabled = emailValue.isNotBlank()
                ) {
                    Text(text = "Forgot Password")
                }
            }
        }
        item {
            Button(
                modifier = Modifier.fillParentMaxWidth(),
                onClick = {

                },
                enabled = emailValue.isNotBlank() && passwordValue.isNotBlank()
            ) {
                Text(text = "Sign In")
            }
        }
        item {
            if (accountViewModel.hasGitHubProvider || accountViewModel.hasGoogleProvider) {
                Row(
                    modifier = Modifier.fillParentMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1F)
                    )
                    Text(text = "Or login with")
                    HorizontalDivider(
                        modifier = Modifier.weight(1F)
                    )
                }
            }
        }
        item {
            FlowRow(
                modifier = Modifier.fillParentMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                if (accountViewModel.hasGitHubProvider) {
                    GitHubButton(
                        modifier = Modifier.weight(1F),
                        onClick = { params ->
                            accountViewModel.githubSignIn(params)
                        },
                        text = "GitHub"
                    )
                }
                if (accountViewModel.hasGoogleProvider) {
                    GoogleButton(
                        modifier = Modifier.weight(1F),
                        onClick = {
                            accountViewModel.googleSignIn()
                        },
                        text = "Google"
                    )
                }
            }
        }
        item {
            Text(
                modifier = Modifier.fillParentMaxWidth(0.75F).padding(top = 16.dp),
                text = "By creating an account you agree to our Terms of Service and Privacy Policy.",
                textAlign = TextAlign.Center
            )
        }
    }
}