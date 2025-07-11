package dev.datlag.mimasu.ui.navigation.detail.person

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.viewmodel.PersonViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import dev.datlag.tooling.async.suspendCatching

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PersonDetail(
    onBack: () -> Unit
) {
    val personViewModel = kodeinViewModel<PersonViewModel>()
    val personState by personViewModel.person.collectAsStateWithLifecycle(PersonViewModel.State.Loading)
    val initialPeople by personViewModel.initialPeople.collectAsStateWithLifecycle()
    val initialCast by personViewModel.initialCast.collectAsStateWithLifecycle()
    val initialCrew by personViewModel.initialCrew.collectAsStateWithLifecycle()

    PredictiveBackHandler(enabled = true) { state ->
        suspendCatching {
            state.collect {
                // Collecting required
            }
            onBack()
        }
    }

    Scaffold { padding ->
        when (val current = personState) {
            is PersonViewModel.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.5F).clip(CircleShape)
                    )
                }
            }
            is PersonViewModel.State.Error -> {
                ErrorState(
                    throwable = current.throwable,
                    additionalInfo = "PersonDetail",
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            }
            is PersonViewModel.State.Success -> {
                PersonContent(
                    person = current.person,
                    initialPeople = initialPeople,
                    initialCast = initialCast,
                    initialCrew = initialCrew,
                    padding = padding,
                    onBack = onBack
                )
            }
        }
    }
}