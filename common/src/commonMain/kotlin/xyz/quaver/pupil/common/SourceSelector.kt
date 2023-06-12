package xyz.quaver.pupil.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import xyz.quaver.pupil.common.decompose.DefaultSourceSelectorComponent
import xyz.quaver.pupil.common.decompose.LocalComponentContext
import xyz.quaver.pupil.common.decompose.SourceSelectorComponent

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
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = { newQuery ->
            query = newQuery
        },
        active = active,
        onActiveChange = { newActive ->
            active = newActive
        },
        onSearch = {

        }
    ) {
        Text("Local")
    }
}

@Composable
fun Explore() {
    Text("Explore")
}

@Composable
private fun SourceSelectorNavigationRail(
    child: SourceSelectorComponent.Child,
    onLocal: () -> Unit,
    onExplore: () -> Unit
) {
    NavigationRail {
        NavigationRailItem(
            selected = child is SourceSelectorComponent.Child.LocalChild,
            onClick = onLocal,
            icon = {
                Icon(Icons.Default.DownloadDone, contentDescription = "Local")
            },
            label = { Text("Local") }
        )

        NavigationRailItem(
            selected = child is SourceSelectorComponent.Child.ExploreChild,
            onClick = onExplore,
            icon = {
                Icon(Icons.Default.Explore, contentDescription = "Explore")
            },
            label = { Text("Explore") }
        )
    }
}

@Composable
private fun SourceSelectorNavigationBar(
    child: SourceSelectorComponent.Child,
    onLocal: () -> Unit,
    onExplore: () -> Unit
) {
    NavigationBar(Modifier.fillMaxWidth()) {
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.DownloadDone, contentDescription = "Local")
            },
            label = { Text("Local") },
            selected = child is SourceSelectorComponent.Child.LocalChild,
            onClick = onLocal
        )
        NavigationBarItem(
            icon = {
                Icon(Icons.Default.Explore, contentDescription = "Explore")
            },
            label = { Text("Explore") },
            selected = child is SourceSelectorComponent.Child.ExploreChild,
            onClick = onExplore
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SourceSelector() {
    val componentContext = LocalComponentContext.current

    val component = remember { DefaultSourceSelectorComponent(componentContext) }

    val windowSizeClass = LocalWindowSizeClass.current

    val navigationType: NavigationType
    val contentType: ContentType

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            navigationType = NavigationType.BOTTOM_NAGIVATION
            contentType = ContentType.SINGLE_PANE
        }

        WindowWidthSizeClass.Medium -> {
            navigationType = NavigationType.NAVIGATION_RAIL
            contentType = ContentType.SINGLE_PANE
        }

        WindowWidthSizeClass.Expanded -> {
            navigationType = NavigationType.NAVIGATION_RAIL
            contentType = ContentType.DUAL_PANE
        }

        else -> {
            navigationType = NavigationType.BOTTOM_NAGIVATION
            contentType = ContentType.SINGLE_PANE
        }
    }

    val stack by component.stack.subscribeAsState()

    val child by derivedStateOf { stack.active.instance }

    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType is NavigationType.NAVIGATION_RAIL) {
            SourceSelectorNavigationRail(
                child,
                component::onLocal,
                component::onExplore
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Children(
                stack = stack,
                animation = stackAnimation(fade() + scale())
            ) { child ->
                when (child.instance) {
                    is SourceSelectorComponent.Child.LocalChild -> Local()
                    is SourceSelectorComponent.Child.ExploreChild -> Explore()
                }
            }
            AnimatedVisibility(visible = navigationType is NavigationType.BOTTOM_NAGIVATION) {
                SourceSelectorNavigationBar(
                    child,
                    component::onLocal,
                    component::onExplore
                )
            }
        }
    }
}