package xyz.quaver.pupil.common.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Source {
    @Composable
    fun Entry()
}

interface SourceEntry {
    val name: String
    val version: String

    val source: Source

    @Composable
    fun Icon(modifier: Modifier)
}