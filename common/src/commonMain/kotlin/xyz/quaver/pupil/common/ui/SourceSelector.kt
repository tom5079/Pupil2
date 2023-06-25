package xyz.quaver.pupil.common.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.icerock.moko.resources.compose.painterResource
import org.kodein.di.compose.localDI
import xyz.quaver.pupil.common.MR
import xyz.quaver.pupil.common.component.DefaultSourceSelectorComponent
import xyz.quaver.pupil.common.component.LocalComponentContext
import xyz.quaver.pupil.common.component.SourceSelectorComponent
import xyz.quaver.pupil.common.inset.systemBars
import xyz.quaver.pupil.common.source.SourceEntry
import xyz.quaver.pupil.common.util.LocalWindowSizeClass
import xyz.quaver.pupil.common.util.WindowWidthSizeClass

private sealed class NavigationType {
    object BOTTOM_NAGIVATION : NavigationType()
    object NAVIGATION_RAIL : NavigationType()
}

private sealed class ContentType {
    object SINGLE_PANE : ContentType()
    object DUAL_PANE : ContentType()
}

@Composable
fun Local(
    sourceList: List<SourceEntry>
) {
    LazyColumn {
        items(sourceList) { source ->
            Card {
                source.Icon(modifier = Modifier.size(32.dp))
            }
        }
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SourceSelectorSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit
) {
    val windowSizeClass = LocalWindowSizeClass.current

    val padding by animateDpAsState(if (active) 0.dp else 8.dp)

    val leadingIcon = @Composable {
        Crossfade(active) {
            if (!active) {
                Image(painterResource(MR.images.vector_icon), "Pupil icon", modifier = Modifier.size(32.dp))
            } else {
                Icon(
                    Icons.Default.ArrowBack,
                    "Pupil icon",
                    modifier = Modifier.size(24.dp).clickable { onActiveChange(false) })
            }
        }
    }

    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
        SearchBar(
            modifier = Modifier.padding(padding).fillMaxWidth(),
            query = query,
            onQueryChange = onQueryChange,
            active = active,
            onActiveChange = onActiveChange,
            onSearch = onSearch,
            leadingIcon = leadingIcon,
            windowInsets = WindowInsets.systemBars
        ) {
            Text("Local")
        }
    } else {
        DockedSearchBar(
            modifier = Modifier.padding(8.dp),
            query = query,
            onQueryChange = onQueryChange,
            active = active,
            onActiveChange = onActiveChange,
            onSearch = onSearch,
            leadingIcon = leadingIcon
        ) {
            Text("Local")
        }
    }
}

@Composable
fun SourceSelector() {
    val componentContext = LocalComponentContext.current

    val di = localDI()
    val component = remember { DefaultSourceSelectorComponent(di, componentContext) }

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

    val searchQuery by component.searchQuery.subscribeAsState()
    val searchBarActive by component.searchBarActive.subscribeAsState()

    val sourceList: List<SourceEntry> by component.sourceListFlow.collectAsState(emptyList())

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
                modifier = Modifier.weight(1f),
                stack = stack,
                animation = stackAnimation(fade() + scale())
            ) { child ->
                SourceSelectorSearchBar(
                    searchQuery,
                    onQueryChange = component::onSearchQueryChange,
                    searchBarActive,
                    onActiveChange = component::onSearchBarActiveChange,
                    onSearch = {}
                )
                Box {
                    when (child.instance) {
                        is SourceSelectorComponent.Child.LocalChild -> Local(sourceList)
                        is SourceSelectorComponent.Child.ExploreChild -> Explore()
                    }
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