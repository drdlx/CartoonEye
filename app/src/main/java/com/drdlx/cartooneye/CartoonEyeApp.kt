package com.drdlx.cartooneye

import android.app.Application
import com.drdlx.cartooneye.di.AppModule
import com.drdlx.cartooneye.di.NavigationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module
import android.util.Log
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes

class CartoonEyeApp: Application() {

    companion object {
        private const val TAG = "CartoonEyeApp"
    }

    override fun onCreate() {
        super.onCreate()
        AppCenter.start(this, BuildConfig.APPCENTER_ACCESS_KEY, Analytics::class.java, Crashes::class.java)
        // Start Koin
        startKoin {
            Log.i(TAG, "Starting Koin")
            androidLogger()
            androidContext(this@CartoonEyeApp)
            modules(
                AppModule().module,
                NavigationModule().module,
            )
        }
    }
}