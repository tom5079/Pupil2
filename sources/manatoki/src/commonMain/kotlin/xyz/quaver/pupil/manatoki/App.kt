package xyz.quaver.pupil.manatoki

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import xyz.quaver.pupil.common.source.Source

class App : Source {
    @Composable
    override fun Entry() {
        var text by remember { mutableStateOf("Hello, World!") }
        val platformName = getPlatformName()

        Button(onClick = {
            text = "Hello, ${platformName}"
        }) {
            Text(text)
        }
    }
}

