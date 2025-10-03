package com.ucb.proyectofinalgamerteca

import android.app.Application
import com.ucb.proyectofinalgamerteca.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class GamertecaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GamertecaApp)
            modules(appModule)
        }
    }
}