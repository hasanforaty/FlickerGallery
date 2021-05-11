package com.hasan.foraty.photogallery.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.hasan.foraty.photogallery.R

const val NOTIFICATION_CHANNEL_ID_DEFAULT = "flickerDefault"
class PhotoGalleryApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID_DEFAULT,
                getString(R.string.notification_channel_default_name),
                importance
            )
//            val notificationManager:NotificationManager =
//                getSystemService(NotificationManager::class.java)
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.createNotificationChannel(channel)
        }

    }
    companion object{
        fun newIntent(context: Context) :Intent = Intent(context,PhotoGalleryApplication::class.java)
    }
}