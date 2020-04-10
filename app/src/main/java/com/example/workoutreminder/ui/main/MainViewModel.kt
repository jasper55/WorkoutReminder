package com.example.workoutreminder.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.BackoffPolicy
import com.example.workoutreminder.NotifyWorker
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest


class MainViewModel : ViewModel() {

    private val _notificationsEnabled = MutableLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean>
        get() = _notificationsEnabled

    fun schedulePeriodicWork(context: Context) {
        val repeatInterval = 30L
        val timeUnit = TimeUnit.MINUTES

        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(NotifyWorker::class.java, repeatInterval, timeUnit)
                .addTag(periodicWorkTag)
                .setInitialDelay(repeatInterval, timeUnit)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                periodicWorkTag,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
            )
    }

    fun cancelWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(periodicWorkTag)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    companion object {
        val periodicWorkTag = "periodic notification workout work"
    }

}
