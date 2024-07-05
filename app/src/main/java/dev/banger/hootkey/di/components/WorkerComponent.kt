package dev.banger.hootkey.di.components

import dagger.Subcomponent
import dev.banger.hootkey.HootKeyApp

@Subcomponent
interface WorkerComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): WorkerComponent
    }

    fun inject(application: HootKeyApp)
}