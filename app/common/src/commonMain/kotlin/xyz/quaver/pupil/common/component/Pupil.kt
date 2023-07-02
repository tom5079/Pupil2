package xyz.quaver.pupil.common.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.kodein.di.DI
import org.kodein.di.DIAware
import xyz.quaver.pupil.core.source.SourceLoader

interface PupilComponent {
    val slot: Value<ChildSlot<*, Child>>

    fun onBackPressed()

    fun onSource(sourceLoader: SourceLoader)

    sealed class Child {
        class SourceSelector(val component: SourceSelectorComponent) : Child()

        class Source(di: DI, sourceLoader: SourceLoader) : Child() {
            val source = sourceLoader.loadSource(di) ?: error("failed to load source")
        }
    }
}

class DefaultPupilComponent(
    override val di: DI,
    componentContext: ComponentContext
) : PupilComponent, ComponentContext by componentContext, DIAware {
    private val navigation = SlotNavigation<Config>()

    private val backCallback = BackCallback { onBackPressed() }

    init {
        backHandler.register(backCallback)
    }

    override val slot: Value<ChildSlot<*, PupilComponent.Child>> =
        childSlot(
            source = navigation,
            initialConfiguration = { Config.SourceSelector },
            handleBackButton = false,
            key = "PupilSlot",
            childFactory = ::child
        )

    override fun onSource(sourceLoader: SourceLoader) {
        navigation.activate(Config.Source(sourceLoader))
    }

    override fun onBackPressed() {
        navigation.activate(Config.SourceSelector)
    }

    private fun child(config: Config, componentContext: ComponentContext): PupilComponent.Child =
        when (config) {
            is Config.SourceSelector -> PupilComponent.Child.SourceSelector(sourceSelectorComponent(componentContext))
            is Config.Source -> runCatching {
                PupilComponent.Child.Source(di, config.sourceLoader)
            }.getOrDefault(slot.child!!.instance)
        }

    private fun sourceSelectorComponent(componentContext: ComponentContext): SourceSelectorComponent =
        DefaultSourceSelectorComponent(di, componentContext, ::onSource)

    private sealed interface Config : Parcelable {
        @Parcelize
        object SourceSelector : Config

        @Parcelize
        data class Source(val sourceLoader: SourceLoader) : Config
    }
}