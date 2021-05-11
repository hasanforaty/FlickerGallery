package com.hasan.foraty.photogallery.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hasan.foraty.photogallery.R
import com.hasan.foraty.photogallery.ui.MainActivity
import com.hasan.foraty.photogallery.ui.NOTIFICATION_CHANNEL_ID_DEFAULT
import com.hasan.foraty.photogallery.ui.PhotoGalleryApplication

/**
 * PollWorker class
 */
private const val TAG = "PollWorker"
class PollWorker(private val context: Context, workerParameters: WorkerParameters)
    :Worker(context,workerParameters) {
    val NOTIFICATION_ID = 22
    override fun doWork(): Result {
        val repository=FlickrFetchrRepository.newInstance()
        val queryText = QueryPreference.getStoredQuery(context = context)
        val firstPicId = QueryPreference.getStoredFirstId(context = context)
        val galleryItems : List<GalleryItem> =
            if (queryText.isEmpty()){
                repository
                    .fetchPhotosRequest()
                    .execute()
                    .body()
                    ?.photos
                    ?.galleryItems
            }else{
                repository
                    .searchPhotosRequest(queryText)
                    .execute()
                    .body()
                    ?.photos
                    ?.galleryItems
            } ?: emptyList()
        if (galleryItems.isEmpty()){
            return Result.success()
        }
        val firstGalleryItemId = galleryItems.first().id
        if (firstGalleryItemId == firstPicId){
            Log.d(TAG, "doWork: id match no new pic id = $firstGalleryItemId")
        }else{
            Log.d(TAG, "doWork: id don't match new patch of pic id = $firstGalleryItemId")
            QueryPreference.setStoredFirstId(applicationContext,firstGalleryItemId)

            val intent = MainActivity.newIntent(context)
//            val pendingIntent = PendingIntent.getActivities(
//                context,
//                0,
//                arrayOf(intent),
//                PendingIntent.FLAG_UPDATE_CURRENT)
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
            }
            val resources = context.resources
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_DEFAULT)
                .setTicker(resources.getText(R.string.new_picture_title))
                .setContentTitle(resources.getText(R.string.new_picture_title))
                .setContentText(resources.getText(R.string.new_picture_content))
                .setSmallIcon(R.drawable.ic_stat_new)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            val notificationManager = NotificationManagerCompat.from(context)

            notificationManager.notify(NOTIFICATION_ID,notification)

        }


        return Result.success()
    }

}