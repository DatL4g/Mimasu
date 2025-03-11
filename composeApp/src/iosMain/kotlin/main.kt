import androidx.compose.ui.window.ComposeUIViewController
import dev.datlag.mimasu.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
