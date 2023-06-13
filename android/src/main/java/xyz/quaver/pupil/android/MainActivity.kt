package xyz.quaver.pupil.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.defaultComponentContext
import xyz.quaver.pupil.common.component.ProvideComponentContext
import xyz.quaver.pupil.common.ui.Pupil
import xyz.quaver.pupil.common.util.ProvideWindowSize
import xyz.quaver.pupil.common.util.ProvideWindowSizeClass
import xyz.quaver.pupil.common.util.calculateWindowSizeClass

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootComponentContext = defaultComponentContext()

        setContent {
            val windowSize = LocalConfiguration.current
            val windowSizeClass = calculateWindowSizeClass(this)

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