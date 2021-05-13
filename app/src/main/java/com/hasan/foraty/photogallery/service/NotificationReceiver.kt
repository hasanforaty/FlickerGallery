package com.hasan.foraty.photogallery.service

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.hasan.foraty.photogallery.data.PollWorker

private const val TAG = "NotificationReceiver"
class NotificationReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: receive Broadcast")

        if (resultCode!= Activity.RESULT_OK){
            return
        }
        val requestCode = intent.getIntExtra(PollWorker.REQUEST_CODE,0)
        val notification: Notification? = intent.getParcelableExtra(PollWorker.NOTIFICATION)

        val notificationManagerCompat=NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(requestCode,notification!!)


    }
}