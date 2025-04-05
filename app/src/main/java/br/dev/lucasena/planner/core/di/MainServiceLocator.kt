package br.dev.lucasena.planner.core.di

import android.app.Application
import androidx.room.Room
import br.dev.lucasena.planner.data.database.ActivityDao
import br.dev.lucasena.planner.data.database.ActivityDatabase
import br.dev.lucasena.planner.data.database.DATABASE_NAME
import br.dev.lucasena.planner.data.datasource.ActivityLocalDataSource
import br.dev.lucasena.planner.data.datasource.ActivityLocalDataSourceImpl
import br.dev.lucasena.planner.data.datasource.AuthenticationLocalDataSource
import br.dev.lucasena.planner.data.datasource.AuthenticationLocalDataSourceImpl
import br.dev.lucasena.planner.data.datasource.UserRegistrationLocalDataSource
import br.dev.lucasena.planner.data.datasource.UserRegistrationLocalDataSourceImpl
import kotlinx.coroutines.Dispatchers

object MainServiceLocator {
    private var _application: Application? = null
    private val application: Application get() = _application!!

    val ioDispatcher by lazy {
        Dispatchers.IO
    }

    val mainDispatcher by lazy {
        Dispatchers.Main
    }

    val userRegistrationLocalDataSource: UserRegistrationLocalDataSource by lazy {
        UserRegistrationLocalDataSourceImpl(applicationContext = application.applicationContext)
    }

    val authenticationLocalDataSource: AuthenticationLocalDataSource by lazy {
        AuthenticationLocalDataSourceImpl(applicationContext = application.applicationContext)
    }

    val activityDao: ActivityDao by lazy {
        val database = Room.databaseBuilder(
            application.applicationContext,
            ActivityDatabase::class.java,
            DATABASE_NAME
        ).build()

        database.activityDao()
    }

    val activityLocalDataSource: ActivityLocalDataSource by lazy {
        ActivityLocalDataSourceImpl(activityDao = activityDao)
    }

    fun initialize(application: Application) {
        _application = application
    }

    fun clear() {
        _application = null
    }
}