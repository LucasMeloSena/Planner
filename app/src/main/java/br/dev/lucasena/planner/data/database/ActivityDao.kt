package br.dev.lucasena.planner.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activityentity ORDER BY is_concluded, datetime DESC")
    fun getAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activityentity WHERE uuid = :uuid")
    fun getByUuid(uuid: String): ActivityEntity

    @Query("UPDATE activityentity SET is_concluded = :isConcluded WHERE uuid = :uuid")
    fun updateIsConcluded(uuid: String, isConcluded: Boolean)

    @Update
    fun update(activity: ActivityEntity)

    @Query("DELETE FROM activityentity WHERE uuid = :uuid")
    fun delete(uuid: String)

    @Insert
    fun insert(activity: ActivityEntity)
}