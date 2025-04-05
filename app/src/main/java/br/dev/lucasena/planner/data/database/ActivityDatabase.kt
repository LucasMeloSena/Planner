package br.dev.lucasena.planner.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

const val DATABASE_NAME = "activity_databasr"

@Database(entities = [ActivityEntity::class], version = 1)
abstract class ActivityDatabase: RoomDatabase() {
    abstract fun activityDao(): ActivityDao
}