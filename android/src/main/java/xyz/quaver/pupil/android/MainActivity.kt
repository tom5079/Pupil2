package xyz.quaver.pupil.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import xyz.quaver.pupil.android.source.sourceModule
import xyz.quaver.pupil.common.component.ProvideComponentContext
import xyz.quaver.pupil.common.ui.Pupil
import xyz.quaver.pupil.common.util.ProvideWindowSize
import xyz.quaver.pupil.common.util.ProvideWindowSizeClass
import xyz.quaver.pupil.common.util.calculateWindowSizeClass

class MainActivity : AppCompatActivity(), DIAware {
    override val di by DI.lazy {
        import(androidXModule(application))
        import(sourceModule(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootComponentContext = defaultComponentContext()

        setContent {
            val windowSize = LocalConfiguration.current
            val windowSizeClass = calculateWindowSizeClass(this)

            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            DisposableEffect(systemUiController, useDarkIcons) {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )

                onDispose {}
            }

            ProvideWindowSize(DpSize(windowSize.screenWidthDp.dp, windowSize.screenHeightDp.dp)) {
                ProvideWindowSizeClass(windowSizeClass) {
                    ProvideComponentContext(rootComponentContext) {
                        Pupil()
                    }
                }
            }
        }
    }
}