package dev.banger.hootkey.di.modules

import dagger.Module
import dagger.Provides
import dev.banger.hootkey.di.qualifiers.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
class AppModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}