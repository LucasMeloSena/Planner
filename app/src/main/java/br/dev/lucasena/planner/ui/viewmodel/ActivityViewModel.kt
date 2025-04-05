package br.dev.lucasena.planner.ui.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.dev.lucasena.planner.core.di.MainServiceLocator
import br.dev.lucasena.planner.core.di.MainServiceLocator.ioDispatcher
import br.dev.lucasena.planner.core.di.MainServiceLocator.mainDispatcher
import br.dev.lucasena.planner.data.datasource.ActivityLocalDataSource
import br.dev.lucasena.planner.domain.model.Activity
import br.dev.lucasena.planner.domain.utils.createCalendarFromTimeInMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ActivityViewModel : ViewModel() {
    val activityLocalDataSource: ActivityLocalDataSource by lazy {
        MainServiceLocator.activityLocalDataSource
    }

    private val _activities: MutableStateFlow<List<Activity>> = MutableStateFlow(emptyList())
    val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    private val newActivity: MutableStateFlow<NewActivity> = MutableStateFlow(NewActivity())

    private val activityToUpdate: MutableStateFlow<Activity?> = MutableStateFlow(null)

    fun setSelectedActivity(selectedActivity: Activity) {
        activityToUpdate.value = selectedActivity
    }

    fun clearSelectedActivity() {
        activityToUpdate.value = null
    }

    fun updateSelectedActivity(
        name: String? = null,
        date: SetDate? = null,
        time: SetTime? = null
    ) {
        if (name == null && date == null && time == null) return

        activityToUpdate.update { currentActivity ->
            currentActivity?.let { activity ->
                val datetime = createCalendarFromTimeInMillis(activity.datetime)
                datetime.apply {
                    date?.let {
                        set(Calendar.YEAR, date.year)
                        set(Calendar.MONTH, date.month)
                        set(Calendar.DAY_OF_MONTH, date.day)

                    }
                    time?.let {
                        set(Calendar.HOUR_OF_DAY, time.hour)
                        set(Calendar.MINUTE, time.minute)
                    }
                }

                activity.copy(
                    name = name ?: activity.name,
                    datetime = datetime.timeInMillis
                )
            }
        }
    }

    fun putDataNewActivity(
        name: String? = null,
        date: SetDate? = null,
        time: SetTime? = null
    ) {
        if (name == null && date == null && time == null) return

        newActivity.update { data ->
            data.copy(
                name = name ?: data.name,
                date = date ?: data.date,
                time = time ?: data.time
            )
        }
    }

    fun fetchActivities() {
        viewModelScope.launch {
            activityLocalDataSource.activities
                .flowOn(ioDispatcher)
                .collect { activities ->
                    withContext(mainDispatcher) {
                        _activities.value = activities
                    }
                }
        }

    }

    fun saveNewActivity(onSuccess: () -> Unit, onError: () -> Unit) {
        newActivity.value.let { newActivity ->
            if (newActivity.isFilled()) {
                val calendar = Calendar.getInstance()
                val datetime = calendar.apply {
                    set(Calendar.YEAR, newActivity.date?.year ?: 0)
                    set(Calendar.MONTH, newActivity.date?.month ?: 0)
                    set(Calendar.DAY_OF_MONTH, newActivity.date?.day ?: 0)
                    set(Calendar.HOUR_OF_DAY, newActivity.time?.hour ?: 0)
                    set(Calendar.MINUTE, newActivity.time?.minute ?: 0)
                }.timeInMillis

                insert(
                    name = newActivity.name.orEmpty(),
                    datetime = datetime
                )

                this@ActivityViewModel.newActivity.update { NewActivity() }

                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun updateActivity() {
        activityToUpdate.value?.let { activity ->
            update(activity)
        }
    }

    private fun insert(name: String, datetime: Long) {
        viewModelScope.launch {
            val activity = Activity(
                uuid = UUID.randomUUID().toString(),
                name = name,
                datetime = datetime,
                isConcluded = false
            )
            withContext(ioDispatcher) {
                activityLocalDataSource.insert(activity)
            }
        }
    }

    private fun update(activity: Activity) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                activityLocalDataSource.update(activity)
            }
        }
    }

    fun updateIsConcluded(uuid: String, isConcluded: Boolean) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                activityLocalDataSource.updateIsConcluded(uuid, isConcluded)
            }
        }
    }

    fun delete(uuid: String) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                activityLocalDataSource.delete(uuid)
            }
        }
    }
}