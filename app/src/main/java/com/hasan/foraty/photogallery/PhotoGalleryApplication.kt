package com.hasan.foraty.photogallery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.hasan.foraty.photogallery.R
import com.hasan.foraty.photogallery.common.composition.AppCompostionRoot

const val NOTIFICATION_CHANNEL_ID="flickrNewPic"
class PhotoGalleryApplication:Application() {
    lateinit var appCompositionRoot:AppCompostionRoot
    override fun onCreate() {
        super.onCreate()
        appCompositionRoot = AppCompostionRoot()
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