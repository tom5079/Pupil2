package xyz.quaver.pupil.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import xyz.quaver.pupil.common.ProvideWindowSizeClass
import xyz.quaver.pupil.common.Pupil
import xyz.quaver.pupil.common.calculateWindowSizeClass

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            ProvideWindowSizeClass(windowSizeClass) {
                Pupil()
            }
        }
    }
}