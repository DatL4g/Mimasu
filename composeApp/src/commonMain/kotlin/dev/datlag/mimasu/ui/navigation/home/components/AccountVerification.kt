package dev.datlag.mimasu.ui.navigation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.account_verification
import dev.datlag.mimasu.composeapp.generated.resources.account_verification_check
import dev.datlag.mimasu.composeapp.generated.resources.account_verification_send
import dev.datlag.mimasu.composeapp.generated.resources.account_verification_text
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import dev.datlag.tooling.compose.withMainContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

@Composable
fun AccountVerification(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(user) {
        when (val current = user) {
            null -> { }
            else -> {
                val creationDate = current.creationDate
                if (creationDate != null) {
                    val pastDays = abs(creationDate.daysUntil(Clock.System.now(), TimeZone.currentSystemDefault()))

                    if (pastDays > 60) { // ToDo("Set to 60 for initial release with this setup, lower after one month")
                        accountViewModel.deleteAccount(current)

                        withMainContext {
                            onLogout()
                        }
                    }
                }
            }
        }
    }

    if (user != null && user?.isVerified != true) {
        ElevatedCard(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.account_verification),
                    style = Platform.typography().headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(Res.string.account_verification_text)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val scope = rememberCoroutineScope()
                    var delaySend by rememberSaveable { mutableStateOf(false) }
                    var delayCheck by rememberSaveable { mutableStateOf(false) }

                    LaunchedEffect(delaySend) {
                        if (delaySend) {
                            delay(30.seconds)
                            delaySend = false
                        }
                    }

                    LaunchedEffect(delayCheck) {
                        if (delayCheck) {
                            delay(30.seconds)
                            delayCheck = false
                        }
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                delaySend = true
                                user?.sendVerification()
                            }
                        },
                        enabled = user != null && !delaySend
                    ) {
                        MaterialSymbols(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = MaterialSymbols.MAIL,
                            contentDescription = null,
                            filled = true
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(Res.string.account_verification_send),
                            maxLines = 1
                        )
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                delayCheck = true
                                user?.reload()
                            }
                        },
                        enabled = user != null && !delayCheck
                    ) {
                        MaterialSymbols(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            name = MaterialSymbols.REFRESH,
                            contentDescription = null,
                            filled = true
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            text = stringResource(Res.string.account_verification_check),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}