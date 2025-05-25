package dev.datlag.mimasu.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.extension.AIDLService
import dev.datlag.mimasu.extension.model.Update
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.createAsFileSafely
import dev.datlag.tooling.deleteSafely
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.solrudev.ackpine.installer.InstallFailure
import ru.solrudev.ackpine.installer.PackageInstaller
import ru.solrudev.ackpine.installer.createSession
import ru.solrudev.ackpine.installer.parameters.InstallMode
import ru.solrudev.ackpine.installer.parameters.InstallerType
import ru.solrudev.ackpine.installer.parameters.PackageSource
import ru.solrudev.ackpine.session.Session
import ru.solrudev.ackpine.session.await
import ru.solrudev.ackpine.session.parameters.Confirmation
import java.io.File

class ExtensionUpdateViewModel(
    val filesDir: File?,
    val httpClient: HttpClient,
    val packageInstaller: PackageInstaller
) : ViewModel() {

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    private suspend fun updatePackage(
        uri: Uri,
    ): Session.State.Completed<InstallFailure> {
        val result = suspendCatching {
            packageInstaller.createSession(uri) {
                confirmation = Confirmation.IMMEDIATE
                installerType = InstallerType.SESSION_BASED
                installMode = InstallMode.Full
                packageSource = PackageSource.DownloadedFile
            }.await()
        }

        return result.getOrNull() ?: run {
            val exception = result.exceptionOrNull()
            val failure = (exception as? Exception)?.let(InstallFailure::Exceptional)
                ?: InstallFailure.Generic(exception?.message)

            Session.State.Failed(failure)
        }
    }

    fun update(
        update: Update?
    ) {
        val downloadUrl = update?.downloadUrl?.ifBlank { null } ?: return
        val file = filesDir?.resolve("extension.apk") ?: return

        _state.update { State.Preparing }

        file.deleteSafely()
        file.createAsFileSafely()

        viewModelScope.launch {
            val response = httpClient.prepareGet(downloadUrl) {
                onDownload { bytesSentTotal, contentLength ->
                    _state.update {
                        if (contentLength == null || contentLength <= 0L) {
                            State.Downloading.Unknown
                        } else {
                            State.Downloading.Progress(
                                reached = bytesSentTotal,
                                length = contentLength
                            )
                        }
                    }
                }
            }.execute { response ->
                val channel = response.bodyAsChannel()

                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(BUFFER_SIZE)

                    while (!packet.exhausted()) {
                        val bytes = packet.readByteArray()

                        file.appendBytes(bytes)
                    }
                }

                response
            }

            if (response.status.isSuccess()) {
                _state.update { State.Install.Unknown }
                val installResult = updatePackage(
                    uri = file.toUri()
                )

                when (installResult) {
                    is Session.State.Succeeded -> _state.update {
                        State.Install.Success
                    }
                    is Session.State.Failed -> _state.update {
                        State.Install.Failure(
                            canRetry = installResult.failure is InstallFailure.Aborted
                        )
                    }
                }
            } else {
                _state.update { State.Install.Failure(false) }
            }
            file.deleteSafely()
        }
    }

    fun clearState() = _state.update { null }

    @Serializable
    sealed interface State {

        @Serializable
        data object Preparing : State

        @Serializable
        sealed interface Downloading : State {

            @Serializable
            data object Unknown : Downloading

            @Serializable
            data class Progress(
                val reached: Long,
                val length: Long
            ) : Downloading {

                @Transient
                val percentage: Float = if (length > reached) (reached.toFloat() / length.toFloat()).coerceIn(0F, 1F) else 0F
            }
        }

        @Serializable
        sealed interface Install : State {

            @Serializable
            data object Unknown : Install

            @Serializable
            data object Success : Install

            @Serializable
            data class Failure(
                val canRetry: Boolean
            ) : Install
        }
    }

    companion object {
        private const val BUFFER_SIZE = 1024L
    }
}