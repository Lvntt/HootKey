package dev.banger.hootkey.di.components

import dagger.Subcomponent
import dev.banger.hootkey.service.HootKeyAutofillService

@Subcomponent
interface AutofillComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AutofillComponent
    }

    fun inject(service: HootKeyAutofillService)
}