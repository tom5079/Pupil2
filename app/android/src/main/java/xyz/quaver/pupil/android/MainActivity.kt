package xyz.quaver.pupil.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import org.kodein.di.provider
import xyz.quaver.pupil.android.source.sourceModule
import xyz.quaver.pupil.common.component.DefaultPupilComponent
import xyz.quaver.pupil.common.component.ProvideComponentContext
import xyz.quaver.pupil.common.ui.Pupil
import xyz.quaver.pupil.common.util.ProvideWindowSize
import xyz.quaver.pupil.common.util.ProvideWindowSizeClass
import xyz.quaver.pupil.common.util.calculateWindowSizeClass
import xyz.quaver.pupil.core.source.SourceEntry

class MainActivity : AppCompatActivity(), DIAware {
    override val di by DI.lazy {
        import(androidXModule(application))
        import(sourceModule(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootComponentContext = defaultComponentContext()

        val launchSource = intent.getStringExtra("launchSource")

        setContent {
            val windowSize = LocalConfiguration.current
            val windowSizeClass = calculateWindowSizeClass(this)

            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            val component = remember { DefaultPupilComponent(di, rootComponentContext) }

            LaunchedEffect(Unit) {
                if (launchSource != null) {
                    val loadSources: () -> List<SourceEntry> by provider()

                    val source = loadSources().firstOrNull {
                        it.name == launchSource
                    }

                    if (source != null) {
                        component.onSource(source.sourceLoader)
                    }
                }
            }

            LaunchedEffect(systemUiController, useDarkIcons) {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }

            ProvideWindowSize(DpSize(windowSize.screenWidthDp.dp, windowSize.screenHeightDp.dp)) {
                ProvideWindowSizeClass(windowSizeClass) {
                    ProvideComponentContext(rootComponentContext) {
                        Pupil(component)
                    }
                }
            }
        }
    }
}