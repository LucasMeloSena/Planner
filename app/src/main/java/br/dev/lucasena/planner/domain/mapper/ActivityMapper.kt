package br.dev.lucasena.planner.domain.mapper

import br.dev.lucasena.planner.data.database.ActivityEntity
import br.dev.lucasena.planner.domain.model.Activity

fun ActivityEntity.toDomain(): Activity =
    Activity(
        uuid = this.uuid,
        name = this.name,
        datetime = this.datetime,
        isConcluded = this.isConcluded
    )

fun Activity.toEntity(id: Int): ActivityEntity =
    ActivityEntity(
        id = id,
        uuid = this.uuid,
        name = this.name,
        datetime = this.datetime,
        isConcluded = this.isConcluded
    )