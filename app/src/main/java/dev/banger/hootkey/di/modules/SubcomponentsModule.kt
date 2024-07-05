package dev.banger.hootkey.di.modules

import dagger.Module
import dev.banger.hootkey.di.components.AutofillComponent
import dev.banger.hootkey.di.components.WorkerComponent

@Module(
    subcomponents = [
        AutofillComponent::class,
        WorkerComponent::class
    ]
)
abstract class SubcomponentsModule