package xyz.quaver.pupil.common.inset

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


actual fun Modifier.systemBarsPadding() = this.systemBarsPadding()

actual val WindowInsets.Companion.systemBars: WindowInsets
    @Composable
    get() = WindowInsets.systemBars