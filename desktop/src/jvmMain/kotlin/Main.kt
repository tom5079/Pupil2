import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import xyz.quaver.pupil.common.ProvideWindowSize
import xyz.quaver.pupil.common.ProvideWindowSizeClass
import xyz.quaver.pupil.common.Pupil
import xyz.quaver.pupil.common.WindowSizeClass
import xyz.quaver.pupil.common.decompose.ProvideComponentContext


@OptIn(ExperimentalDecomposeApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)

    application {
        val windowState = rememberWindowState()

        val windowSizeClass by derivedStateOf {
            WindowSizeClass.calculateFromSize(windowState.size)
        }

        LifecycleController(lifecycle, windowState)

        Window(state = windowState, onCloseRequest = ::exitApplication) {
            ProvideWindowSize(windowState.size) {
                ProvideWindowSizeClass(windowSizeClass) {
                    ProvideComponentContext(rootComponentContext) {
                        Pupil()
                    }
                }
            }
        }
    }
}