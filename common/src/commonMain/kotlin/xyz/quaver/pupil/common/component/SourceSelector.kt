package xyz.quaver.pupil.common.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

interface LocalComponent {

}

interface ExploreComponent {

}

class DefaultLocalComponent(
    componentContext: ComponentContext
) : LocalComponent, ComponentContext by componentContext

class DefaultExploreComponent(
    componentContext: ComponentContext
) : ExploreComponent, ComponentContext by componentContext

val LocalComponentContext: ProvidableCompositionLocal<ComponentContext> =
    staticCompositionLocalOf { error("Root component context was not provided") }

@Composable
fun ProvideComponentContext(componentContext: ComponentContext, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalComponentContext provides componentContext, content = content)
}

interface SourceSelectorComponent {
    val stack: Value<ChildStack<*, Child>>

    fun onBackPressed()

    fun onLocal()

    fun onExplore()

    sealed class Child {
        class LocalChild(val component: LocalComponent) : Child()
        class ExploreChild(val component: ExploreComponent) : Child()
    }
}

class DefaultSourceSelectorComponent(
    componentContext: ComponentContext
) : SourceSelectorComponent, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, SourceSelectorComponent.Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.Local,
            handleBackButton = true,
            key = "SourceSelectorStack",
            childFactory = ::child
        )

    override fun onBackPressed() {
        navigation.pop()
    }

    override fun onLocal() {
        navigation.pop()
    }

    override fun onExplore() {
        navigation.push(Config.Explore)
    }

    private fun child(config: Config, componentContext: ComponentContext): SourceSelectorComponent.Child =
        when (config) {
            is Config.Local -> SourceSelectorComponent.Child.LocalChild(localComponent(componentContext))
            is Config.Explore -> SourceSelectorComponent.Child.ExploreChild(exploreComponent(componentContext))
        }

    private fun localComponent(componentContext: ComponentContext): LocalComponent =
        DefaultLocalComponent(componentContext)

    private fun exploreComponent(componentContext: ComponentContext): ExploreComponent =
        DefaultExploreComponent(componentContext)

    @Parcelize
    private sealed interface Config : Parcelable {
        object Local : Config
        object Explore : Config
    }
}