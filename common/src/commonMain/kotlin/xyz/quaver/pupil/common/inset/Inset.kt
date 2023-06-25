package xyz.quaver.pupil.common.inset

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.Modifier


expect fun Modifier.systemBarsPadding(): Modifier

expect val WindowInsets.Companion.systemBars: WindowInsets