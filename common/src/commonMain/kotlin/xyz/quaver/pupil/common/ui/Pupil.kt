package xyz.quaver.pupil.common.ui

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import xyz.quaver.pupil.common.component.PupilComponent
import xyz.quaver.pupil.common.theme.PupilTheme

@Composable
fun Pupil(component: PupilComponent) {
    PupilTheme {
        val slot by component.slot.subscribeAsState()

        Crossfade(slot.child!!.instance) { child ->
            when (child) {
                is PupilComponent.Child.SourceSelector -> SourceSelector(child.component)
                is PupilComponent.Child.Source -> child.source.Entry()
            }
        }
    }
}
