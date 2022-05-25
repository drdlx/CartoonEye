package com.drdlx.cartooneye

import android.app.Application
import com.drdlx.cartooneye.di.AppModule
import com.drdlx.cartooneye.di.NavigationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module

class CartoonEyeApp: Application() {

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            println("Starting koin!")
            androidLogger()
            androidContext(this@CartoonEyeApp)
            modules(
                AppModule().module,
                NavigationModule().module,
            )
        }
    }
}