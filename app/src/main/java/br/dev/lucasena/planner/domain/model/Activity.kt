package br.dev.lucasena.planner.domain.model

import android.icu.util.Calendar
import br.dev.lucasena.planner.domain.utils.createCalendarFromTimeInMillis
import br.dev.lucasena.planner.domain.utils.toActivityDate
import br.dev.lucasena.planner.domain.utils.toActivityDatetime
import br.dev.lucasena.planner.domain.utils.toActivityTime

data class Activity(
    val uuid: String,
    val name: String,
    val datetime: Long,
    val isConcluded: Boolean
) {
    private val datetimeCalendar: Calendar = createCalendarFromTimeInMillis(datetime)

    val dateString: String = datetimeCalendar.toActivityDate()
    val timeString: String = datetimeCalendar.toActivityTime()
    val datetimeString: String = datetimeCalendar.toActivityDatetime()
}