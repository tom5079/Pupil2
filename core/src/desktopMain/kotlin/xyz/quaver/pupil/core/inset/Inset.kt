package xyz.quaver.pupil.core.inset

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual fun Modifier.systemBarsPadding(): Modifier = this

actual val WindowInsets.Companion.systemBars: WindowInsets
    @Composable
    get() = WindowInsets(0)