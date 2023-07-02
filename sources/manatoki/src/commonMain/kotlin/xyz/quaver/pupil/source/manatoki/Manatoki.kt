package xyz.quaver.pupil.source.manatoki

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import xyz.quaver.pupil.core.source.Source

class Manatoki : Source {
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

