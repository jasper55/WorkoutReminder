package com.example.workoutreminder.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workoutreminder.NotifyWorker
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest


class MainViewModel : ViewModel() {

    val periodicWorkTag = "periodic notification workout work"

    private val _notificationsEnabled = MutableLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean>
        get() = _notificationsEnabled


    init {
        _notificationsEnabled.value = false
    }


    fun schedulePeriodicWork(context: Context) {
        val repeatInterval = 30L
        val timeUnit = TimeUnit.MINUTES

        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(NotifyWorker::class.java, repeatInterval, timeUnit)
                .setInitialDelay(repeatInterval, timeUnit)
        val request = periodicWorkRequest.build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(periodicWorkTag, ExistingPeriodicWorkPolicy.KEEP, request)


//        val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
//            .setInitialDelay(repeatInterval, timeUnit)
////            .setInputData(inputData)
//            .addTag(periodicWorkTag)
//            .build()
//
//        WorkManager.getInstance(context).enqueue(notificationWork)
    }

    fun cancelWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(periodicWorkTag)
    }

    fun setNotificationsEnabled(enabled: Boolean){
        _notificationsEnabled.value = enabled
    }

}
