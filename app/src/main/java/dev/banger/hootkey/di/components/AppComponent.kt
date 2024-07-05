package dev.banger.hootkey.di.components

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.banger.hootkey.di.viewmodel.ViewModelFactoryModule
import dev.banger.hootkey.di.modules.AppModule
import dev.banger.hootkey.di.modules.DataModule
import dev.banger.hootkey.di.modules.DomainModule
import dev.banger.hootkey.di.modules.PresentationModule
import dev.banger.hootkey.di.modules.SubcomponentsModule
import dev.banger.hootkey.presentation.ui.MainActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ViewModelFactoryModule::class,
        PresentationModule::class,
        DomainModule::class,
        DataModule::class,
        AppModule::class,
        SubcomponentsModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance context: Context,
        ): AppComponent
    }

    fun inject(activity: MainActivity)

    fun autofillComponent(): AutofillComponent.Factory
    fun workerComponent(): WorkerComponent.Factory
}