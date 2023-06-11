package xyz.quaver.pupil.manatoki

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    val platformName = getPlatformName()

    Button(onClick = {
        text = "Hello, ${platformName}"
    }) {
        Text(text)
    }
}
