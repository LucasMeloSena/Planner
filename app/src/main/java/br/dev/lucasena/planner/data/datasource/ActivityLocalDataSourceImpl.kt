package br.dev.lucasena.planner.data.datasource

import br.dev.lucasena.planner.data.database.ActivityDao
import br.dev.lucasena.planner.domain.mapper.toDomain
import br.dev.lucasena.planner.domain.mapper.toEntity
import br.dev.lucasena.planner.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivityLocalDataSourceImpl(
    private val activityDao: ActivityDao
): ActivityLocalDataSource {
    override val activities: Flow<List<Activity>>
        get() = activityDao.getAll().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }

    override suspend fun getByUuid(uuid: String): Activity {
        return activityDao.getByUuid(uuid).toDomain()
    }

    override suspend fun updateIsConcluded(uuid: String, isConcluded: Boolean) {
        activityDao.updateIsConcluded(uuid, isConcluded)
    }

    override suspend fun update(activity: Activity) {
        val entity = activityDao.getByUuid(activity.uuid)
        activityDao.update(activity.toEntity(entity.id))
    }

    override suspend fun delete(uuid: String) {
        activityDao.delete(uuid)
    }

    override suspend fun insert(activity: Activity) {
        activityDao.insert(activity.toEntity(0))
    }
}