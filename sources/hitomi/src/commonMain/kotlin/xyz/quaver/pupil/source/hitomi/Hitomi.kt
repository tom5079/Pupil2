package xyz.quaver.pupil.source.hitomi

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import xyz.quaver.pupil.common.source.Source

class Hitomi : Source {
    @Composable
    override fun Entry() {
        var text by remember { mutableStateOf("Hello, World!") }

        Button(onClick = {
            text = "Hello, Hitomi"
        }) {
            Text(text)
        }
    }
}

