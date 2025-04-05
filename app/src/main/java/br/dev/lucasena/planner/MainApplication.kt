package br.dev.lucasena.planner

import android.app.Application
import br.dev.lucasena.planner.core.di.MainServiceLocator

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MainServiceLocator.initialize(application = this)
    }
}