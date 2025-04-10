package dev.datlag.mimasu.ui.navigation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.BuildKonfig
import dev.datlag.mimasu.Sekret
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
            Text(text = "Google Provider: ${accountViewModel.hasGoogleProvider}, GitHub Provider: ${accountViewModel.hasGitHubProvider}")
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
            Button(
                onClick = {

                },
                enabled = emailValue.isNotBlank() && passwordValue.isNotBlank()
            ) {
                Text(text = "Sign In")
            }
        }
        item {
            FlowRow(
                modifier = Modifier.fillParentMaxWidth(),
                maxItemsInEachRow = 2
            ) {

            }
        }
    }
}