package xyz.quaver.pupil.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

private sealed class Screen : Parcelable {
    @Parcelize
    object Local : Screen()

    @Parcelize
    object Explore : Screen()
}

@Composable
fun Local() {

}

@Composable
fun Explore() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceSelector() {
    var screenState by remember { mutableStateOf<Screen>(Screen.Local) }

    val windowSizeClass = LocalWindowSizeClass.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pupil")
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(Icons.Default.DownloadDone, contentDescription = "Local")
                    },
                    label = { Text("Local") },
                    selected = screenState is Screen.Local,
                    onClick = {
                        screenState = Screen.Local
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(Icons.Default.Explore, contentDescription = "Explore")
                    },
                    label = { Text("Explore") },
                    selected = screenState is Screen.Explore,
                    onClick = {
                        screenState = Screen.Explore
                    }
                )
            }
        }
    ) {
        when (val screen = screenState) {
            is Screen.Local -> Local()
            is Screen.Explore -> Explore()
        }
    }
}