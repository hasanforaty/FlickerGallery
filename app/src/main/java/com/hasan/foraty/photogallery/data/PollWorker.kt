package com.hasan.foraty.photogallery.data

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * PollWorker class
 */
private const val TAG = "PollWorker"
class PollWorker(context: Context, workerParameters: WorkerParameters)
    :Worker(context,workerParameters) {
    override fun doWork(): Result {
        val repository=FlickrFetchrRepository.newInstance()
        val queryText = QueryPreference.getStoredQuery(context = applicationContext)
        val firstPicId = QueryPreference.getStoredFirstId(context = applicationContext)
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
            Log.d(TAG, "doWork: id match no new pic id = ${firstGalleryItemId}")
        }else{
            Log.d(TAG, "doWork: id don't match new patch of pic id = ${firstGalleryItemId}")
            QueryPreference.setStoredFirstId(applicationContext,firstGalleryItemId)
        }


        return Result.success()
    }

}