package br.dev.lucasena.planner.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ActivityEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val uuid: String,
    val name: String,
    val datetime: Long,

    @ColumnInfo("is_concluded")
    val isConcluded: Boolean
)