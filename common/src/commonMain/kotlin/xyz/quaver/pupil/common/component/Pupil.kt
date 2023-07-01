package xyz.quaver.pupil.common.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.parcelable.Parcelable
import org.kodein.di.DI
import org.kodein.di.DIAware
import xyz.quaver.pupil.common.source.Source

interface PupilComponent {
    val slot: Value<ChildSlot<*, Child>>

    fun onBackPressed()

    fun onSource(source: Source)

    sealed class Child {
        class SourceSelector(val component: SourceSelectorComponent) : Child()

        class Source(val source: xyz.quaver.pupil.common.source.Source) : Child()
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

    override fun onSource(source: Source) {
        navigation.activate(Config.Source(source))
    }

    override fun onBackPressed() {
        navigation.activate(Config.SourceSelector)
    }

    private fun child(config: Config, componentContext: ComponentContext): PupilComponent.Child =
        when (config) {
            is Config.SourceSelector -> PupilComponent.Child.SourceSelector(sourceSelectorComponent(componentContext))
            is Config.Source -> PupilComponent.Child.Source(config.source)
        }

    private fun sourceSelectorComponent(componentContext: ComponentContext): SourceSelectorComponent =
        DefaultSourceSelectorComponent(di, componentContext, ::onSource)

    private sealed interface Config : Parcelable {
        object SourceSelector : Config
        data class Source(val source: xyz.quaver.pupil.common.source.Source) : Config
    }
}