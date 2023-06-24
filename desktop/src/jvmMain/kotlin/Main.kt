import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import org.kodein.di.compose.withDI
import source.sourceModule
import xyz.quaver.pupil.common.component.ProvideComponentContext
import xyz.quaver.pupil.common.ui.Pupil
import xyz.quaver.pupil.common.util.ProvideWindowSize
import xyz.quaver.pupil.common.util.ProvideWindowSizeClass
import xyz.quaver.pupil.common.util.WindowSizeClass


@OptIn(ExperimentalDecomposeApi::class, ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()
    val backDispatcher = BackDispatcher()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle, backHandler = backDispatcher)

    application {
        withDI(sourceModule) {
            val windowState = rememberWindowState()

            val windowSizeClass by derivedStateOf {
                WindowSizeClass.calculateFromSize(windowState.size)
            }

            LifecycleController(lifecycle, windowState)

            Window(
                state = windowState,
                onCloseRequest = ::exitApplication,
                onKeyEvent = onKeyEvent@{ keyEvent ->
                    if (keyEvent.key == Key.Escape) {
                        backDispatcher.back()
                        return@onKeyEvent true
                    }

                    false
                }
            ) {
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
}