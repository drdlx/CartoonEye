package com.drdlx.cartooneye.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.annotation.Singleton

@Module
@ComponentScan("com.drdlx.cartooneye")
class AppModule {

    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

}