package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.localTextStyle

@Composable
fun EMailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = Platform.localTextStyle(),
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource? = null
) {
    OutlinedTextField(
        modifier = modifier.semantics {
            contentType = ContentType.EmailAddress
            contentDataType = ContentDataType.Text
        },
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            MaterialSymbols(
                name = MaterialSymbols.MAIL,
                contentDescription = null,
            )
        },
        label = label,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        maxLines = 1,
        singleLine = true,
        isError = isError,
        interactionSource = interactionSource
    )
}