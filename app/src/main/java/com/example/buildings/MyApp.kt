package com.example.buildings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.buildings.di.databaseModule
import com.example.buildings.di.domainAppModule
import com.example.buildings.di.presentationAppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate(){
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        GlobalContext.startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApp)
            modules(databaseModule, presentationAppModule, domainAppModule)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
