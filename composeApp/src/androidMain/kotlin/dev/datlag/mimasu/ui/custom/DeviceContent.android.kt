package dev.datlag.mimasu.ui.custom

import androidx.car.app.connection.CarConnection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.asFlow
import dev.datlag.tooling.decompose.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.flowOf
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull

@Composable
actual fun CarContent(
    detectProjection: Boolean,
    car: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val carConnection by localDI().instanceOrNull<CarConnection>()
    val type by remember(carConnection) {
        carConnection?.type?.asFlow() ?: flowOf(CarConnection.CONNECTION_TYPE_NOT_CONNECTED)
    }.collectAsStateWithLifecycle(CarConnection.CONNECTION_TYPE_NOT_CONNECTED)

    when (type) {
        CarConnection.CONNECTION_TYPE_NATIVE -> car()
        CarConnection.CONNECTION_TYPE_PROJECTION -> if (detectProjection) car() else content()
        else -> content()
    }
}