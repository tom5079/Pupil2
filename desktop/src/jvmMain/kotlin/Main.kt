import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import xyz.quaver.pupil.common.component.ProvideComponentContext
import xyz.quaver.pupil.common.source.Source
import xyz.quaver.pupil.common.util.ProvideWindowSize
import xyz.quaver.pupil.common.util.ProvideWindowSizeClass
import xyz.quaver.pupil.common.util.WindowSizeClass
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile


@OptIn(ExperimentalDecomposeApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)

    val path = "${System.getProperty("user.home")}/.pupil/manatoki.jar"
    val jar = JarFile(path)
    val classLoader = URLClassLoader.newInstance(arrayOf(URL("jar:file:$path!/")))
    val entries = jar.entries()

    val classes = buildList<Source> {
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()

            if (entry.isDirectory || !entry.name.endsWith(".class")) {
                continue
            }

            val className = entry.name.let { it.substring(0, it.length - 6) }.replace('/', '.')
            val klass = runCatching {
                classLoader.loadClass(className).getDeclaredConstructor().newInstance()
            }.getOrNull() as? Source

            if (klass != null) {
                add(klass)
            }
        }
    }

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
                        classes.first().Entry()
                    }
                }
            }
        }
    }
}