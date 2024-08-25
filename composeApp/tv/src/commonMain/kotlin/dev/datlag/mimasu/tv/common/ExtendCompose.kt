package dev.datlag.mimasu.tv.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import kotlinx.collections.immutable.ImmutableList


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.autoFill(
    types: ImmutableList<AutofillType>,
    onFill: (String) -> Unit
): Modifier {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(
        autofillTypes = types,
        onFill = onFill
    )

    LocalAutofillTree.current += autofillNode

    return this.onGloballyPositioned {
        autofillNode.boundingBox = it.boundsInWindow()
    }.onFocusChanged { focusState ->
        with(autofill) {
            if (focusState.isFocused) {
                this?.requestAutofillForNode(autofillNode)
            } else {
                this?.cancelAutofillForNode(autofillNode)
            }
        }
    }
}