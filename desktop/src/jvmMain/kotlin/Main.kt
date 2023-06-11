import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import xyz.quaver.pupil.common.Pupil


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Pupil()
    }
}
