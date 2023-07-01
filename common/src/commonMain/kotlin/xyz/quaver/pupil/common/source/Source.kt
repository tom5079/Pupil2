package xyz.quaver.pupil.common.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import org.kodein.di.DI

interface Source {
    @Composable
    fun Entry()
}

interface SourceLoader : Parcelable {
    fun loadSource(di: DI): Source?
}

interface SourceEntry {
    val name: String
    val version: String

    val sourceLoader: SourceLoader

    @Composable
    fun Icon(modifier: Modifier)
}