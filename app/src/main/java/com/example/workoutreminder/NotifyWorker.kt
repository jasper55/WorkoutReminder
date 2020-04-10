package com.example.workoutreminder

import android.content.Context
import androidx.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotifyWorker(@NonNull context: Context, @NonNull params: WorkerParameters) :
    Worker(context, params) {

    val context = context

    @NonNull
    override fun doWork(): Result {
        // Method to trigger an instant notification
        triggerNotification(context)

        return Result.success()
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    private fun triggerNotification(context: Context) {
                    val notificationBuilder = NotificationBuilder()
            notificationBuilder.createNotification(context)
    }
}