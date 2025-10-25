package dev.datlag.mimasu

import android.os.Bundle
import androidx.activity.compose.setContent
import dev.datlag.mimasu.ui.space.SpaceContent
import dev.datlag.mimasu.ui.space.SpaceTheme

class SpaceActivity : MimasuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpaceTheme {
                SpaceContent()
            }
        }
    }
}

