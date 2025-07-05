package dev.datlag.mimasu.tv.ui.navigation.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import dev.datlag.mimasu.ui.custom.login.Login
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.localTextStyle

@Composable
fun Login(
    appImage: Painter,
    onSuccess: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        colors = SurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Login(
            appImage = appImage,
            modifier = Modifier.fillMaxSize(),
            textStyle = Platform.localTextStyle().copy(color = MaterialTheme.colorScheme.onBackground),
            onSuccess = onSuccess
        )
    }
}