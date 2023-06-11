import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import xyz.quaver.pupil.common.App


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
