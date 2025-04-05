package br.dev.lucasena.planner.domain.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.Locale

private val sdfActivityDateTime = SimpleDateFormat("EEE dd'\n'HH:mm", Locale("pt", "BR"))
private val sdfActivityDate = SimpleDateFormat("dd 'de' MMMM", Locale("pt", "BR"))
private val sdfActivityTime = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

fun createCalendarFromTimeInMillis(timeInMs: Long): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMs
    return calendar
}

fun Calendar.toActivityDatetime(): String = sdfActivityDateTime.format(this.time)
fun Calendar.toActivityDate(): String = sdfActivityDate.format(this.time)
fun Calendar.toActivityTime(): String = sdfActivityTime.format(this.time)

