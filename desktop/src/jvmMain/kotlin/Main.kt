import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import xyz.quaver.pupil.common.ProvideWindowSizeClass
import xyz.quaver.pupil.common.Pupil
import xyz.quaver.pupil.common.WindowSizeClass


fun main() = application {
    val windowState = rememberWindowState()

    val windowSizeClass by derivedStateOf {
        WindowSizeClass.calculateFromSize(windowState.size)
    }

    Window(state = windowState, onCloseRequest = ::exitApplication) {
        ProvideWindowSizeClass(windowSizeClass) {
            Pupil()
        }
    }
}
