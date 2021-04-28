package com.hasan.foraty.photogallery.common.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.hasan.foraty.photogallery.R

const val NOTIFICATION_CHANNEL_ID="flickrNewPic"
class PhotoGalleryApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID
                ,name
                ,importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


    }
}