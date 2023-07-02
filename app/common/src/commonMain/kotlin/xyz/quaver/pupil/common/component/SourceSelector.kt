package xyz.quaver.pupil.common.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.provider
import xyz.quaver.pupil.core.source.SourceEntry
import xyz.quaver.pupil.core.source.SourceLoader
import kotlin.time.Duration.Companion.seconds

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

    val searchQuery: Value<String>
    val searchBarActive: Value<Boolean>

    val sourceListFlow: Flow<List<SourceEntry>>

    val onSource: (SourceLoader) -> Unit


    fun onBackPressed()

    fun onLocal()

    fun onExplore()

    fun onSearchQueryChange(newQuery: String)

    fun onSearchBarActiveChange(newActive: Boolean)

    sealed class Child {
        class LocalChild(val component: LocalComponent) : Child()
        class ExploreChild(val component: ExploreComponent) : Child()
    }
}

class DefaultSourceSelectorComponent(
    override val di: DI,
    componentContext: ComponentContext,
    override val onSource: (SourceLoader) -> Unit
) : SourceSelectorComponent, ComponentContext by componentContext, DIAware {

    private val backCallback = BackCallback { onBackPressed() }

    init {
        backHandler.register(backCallback)
    }

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, SourceSelectorComponent.Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.Local,
            handleBackButton = false,
            key = "SourceSelectorStack",
            childFactory = ::child
        )
    private val _searchQuery = MutableValue("")
    private val _searchBarActive = MutableValue(false)

    override val searchQuery = _searchQuery as Value<String>
    override val searchBarActive = _searchBarActive as Value<Boolean>

    private val sourceListProvider: () -> List<SourceEntry> by provider()

    override val sourceListFlow = flow {
        while (true) {
            emit(sourceListProvider())
            delay(1.seconds)
        }
    }.flowOn(Dispatchers.IO)


    override fun onBackPressed() {
        println(searchBarActive.value)
        if (searchBarActive.value) {
            _searchBarActive.value = false
            return
        }
        navigation.pop()
    }

    override fun onLocal() {
        navigation.pop()
    }

    override fun onExplore() {
        navigation.push(Config.Explore)
    }

    override fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    override fun onSearchBarActiveChange(newActive: Boolean) {
        _searchBarActive.value = newActive
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