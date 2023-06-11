package xyz.quaver.pupil.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

private sealed class Screen {
    object Local : Screen()

    object Explore : Screen()
}

private sealed class NavigationType {
    object BOTTOM_NAGIVATION : NavigationType()
    object NAVIGATION_RAIL : NavigationType()
}

private sealed class ContentType {
    object SINGLE_PANE : ContentType()
    object DUAL_PANE : ContentType()
}

@Composable
fun Local() {
    Text("Local")
}

@Composable
fun Explore() {
    Text("Explore")
}

@Composable
private fun SourceSelectorNavigationRail(screen: Screen, onClick: (Screen) -> Unit) {
    NavigationRail {
        NavigationRailItem(
            selected = screen is Screen.Local,
            onClick = {
                onClick(Screen.Local)
            },
            icon = {
                Icon(Icons.Default.DownloadDone, contentDescription = "Local")
            },
            label = { Text("Local") }
        )

        NavigationRailItem(
            selected = screen is Screen.Explore,
            onClick = {
                onClick(Screen.Explore)
            },
            icon = {
                Icon(Icons.Default.Explore, contentDescription = "Explore")
            },
            label = { Text("Explore") }
        )
    }
}

@Composable
private fun SourceSelectorNavigationBar(screen: Screen, onClick: (Screen) -> Unit) {
    NavigationBar(Modifier.fillMaxWidth()) {
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.DownloadDone, contentDescription = "Local")
            },
            label = { Text("Local") },
            selected = screen is Screen.Local,
            onClick = {
                onClick(Screen.Local)
            }
        )
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Explore, contentDescription = "Explore")
            },
            label = { Text("Explore") },
            selected = screen is Screen.Explore,
            onClick = {
                onClick(Screen.Explore)
            }
        )
    }
}

@Composable
fun SourceSelector() {
    var screenState by remember { mutableStateOf<Screen>(Screen.Local) }

    val windowSizeClass = LocalWindowSizeClass.current

    val navigationType = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> NavigationType.BOTTOM_NAGIVATION
        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> NavigationType.NAVIGATION_RAIL
        else -> NavigationType.BOTTOM_NAGIVATION
    }

    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType is NavigationType.NAVIGATION_RAIL) {
            SourceSelectorNavigationRail(
                screenState,
                onClick = { newScreen ->
                    screenState = newScreen
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Box(Modifier.weight(1f)) {
                when (screenState) {
                    is Screen.Local -> Local()
                    is Screen.Explore -> Explore()
                }
            }
            AnimatedVisibility(visible = navigationType is NavigationType.BOTTOM_NAGIVATION) {
                SourceSelectorNavigationBar(
                    screenState,
                    onClick = { newScreen ->
                        screenState = newScreen
                    }
                )
            }
        }
    }
}