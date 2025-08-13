package dev.datlag.mimasu.ui.navigation.profile

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.profile
import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import io.tolgee.stringResource

fun NavigationSuiteScope.profileItem(
    selected: Boolean,
    user: User?,
    onClick: () -> Unit
) = item(
    selected = selected,
    onClick = onClick,
    icon = {
        var fallback by remember(user?.email) { mutableStateOf(false) }

        if (fallback) {
            MaterialSymbols(
                name = MaterialSymbols.PERSON_PIN_CIRCLE,
                contentDescription = null,
                filled = selected
            )
        } else {
            AsyncImage(
                modifier = Modifier.size(24.dp).clip(CircleShape),
                model = user?.profilePictures?.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = user?.profilePictures.orEmpty(),
                    contentScale = ContentScale.Crop,
                    onError = {
                        fallback = true
                    }
                ),
                placeholder = MaterialSymbols.rememberPainter(
                    name = MaterialSymbols.PERSON_PIN_CIRCLE,
                    filled = selected
                ),
                onLoading = {
                    fallback = false
                },
                onSuccess = {
                    fallback = false
                }
            )
        }
    },
    label = {
        Text(
            text = user?.name ?: stringResource(Res.string.profile),
            maxLines = 1,
            softWrap = true,
            overflow = TextOverflow.Ellipsis
        )
    }
)