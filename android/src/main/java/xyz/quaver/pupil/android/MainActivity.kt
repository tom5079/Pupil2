package xyz.quaver.pupil.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.defaultComponentContext
import xyz.quaver.pupil.common.ProvideWindowSize
import xyz.quaver.pupil.common.ProvideWindowSizeClass
import xyz.quaver.pupil.common.Pupil
import xyz.quaver.pupil.common.calculateWindowSizeClass
import xyz.quaver.pupil.common.decompose.ProvideComponentContext

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