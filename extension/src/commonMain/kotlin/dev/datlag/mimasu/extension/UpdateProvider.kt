package dev.datlag.mimasu.extension

import dev.datlag.mimasu.extension.model.Update
import kotlinx.coroutines.flow.StateFlow

interface UpdateProvider {

    val update: StateFlow<Update?>
}