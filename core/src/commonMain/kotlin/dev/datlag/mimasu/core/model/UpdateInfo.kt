package dev.datlag.mimasu.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface UpdateInfo {

    val available: StateFlow<Boolean>
    val required: StateFlow<Boolean>
    val appInfo: StateFlow<AppInfo?>
    val storeURL: StateFlow<String?>
    val directDownload: StateFlow<String?>

    companion object Default : UpdateInfo {
        override val available: StateFlow<Boolean> = MutableStateFlow(false)
        override val required: StateFlow<Boolean> = MutableStateFlow(false)
        override val appInfo: StateFlow<AppInfo?> = MutableStateFlow(null)
        override val storeURL: StateFlow<String?> = MutableStateFlow(null)
        override val directDownload: StateFlow<String?> = MutableStateFlow(null)
    }
}