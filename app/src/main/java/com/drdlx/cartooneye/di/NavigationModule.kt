package com.drdlx.cartooneye.di

import com.drdlx.cartooneye.navigation.AppNavigation
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class NavigationModule() {

    @Singleton
    fun provideNavigation(): AppNavigation = AppNavigation()
}
