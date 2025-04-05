package br.dev.lucasena.planner.data.datasource

import br.dev.lucasena.planner.data.database.ActivityEntity
import br.dev.lucasena.planner.domain.model.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityLocalDataSource {
    val activities: Flow<List<Activity>>

    suspend fun getByUuid(uuid: String): Activity

    suspend fun updateIsConcluded(uuid: String, isConcluded: Boolean)

    suspend fun update(activity: Activity)

    suspend fun delete(uuid: String)

    suspend fun insert(activity: Activity)
}