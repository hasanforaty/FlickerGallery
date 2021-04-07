package com.hasan.foraty.photogallery.data

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.util.LruCache
import androidx.core.util.lruCache
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD=0
/**
 * @param responseHandler main handler that response will send to
 * @param onThumbnailDownloader an interface for patch Bitmap  to T
 */
class ThumbnailDownloader<in T>(
  private val responseHandler: Handler,
  private val onThumbnailDownloader: (T , Bitmap) -> Unit
) :HandlerThread(TAG) {

    /**
     * fragmentLifecycleObserver an object for fragment
     * connected to ThumbnailDownloader class
     */
    val fragmentLifecycleObserver:LifecycleObserver = object : LifecycleObserver{
        //method need for making this class lifecycle aware
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup(){
            Log.i(TAG, "setup: Starting Background Thread")
            start()
            looper
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown(){
            Log.i(TAG, "tearDown: Destroy Background Thread")
            quit()
        }
    }
    /**
     * viewLifecycleObserver an object for View lifecycle of class
     * that connect to this class (ThumbnailDownloader)
     */
    val viewLifecycleObserver:LifecycleObserver=object :LifecycleObserver{
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearQueue(){
            Log.i(TAG, "clearQueue: Clear all request from queue")
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    private var hasQuit = false
    private val repository=FlickrFetchrRepository()
    private val requestMap = ConcurrentHashMap<T,String>()
    private lateinit var requestHandler:Handler
    // instance of LruCache for saving last 100 pic in lruCache
    private val cache = lruCache<String,Bitmap>(100)
    //override method to change hasQuit when leaving Handler
    override fun quit(): Boolean {
        hasQuit=true
        return super.quit()
    }
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler=object :Handler(){
            override fun handleMessage(msg: Message) {
                if (msg.what== MESSAGE_DOWNLOAD){
                    val target=msg.obj as T
                    Log.i(TAG, "handleMessage: Got a request for Url = ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }
    private fun handleRequest(target: T){
        val url = requestMap[target] ?:return
        val bitmap = repository.fetchPhoto(url) ?: return

        responseHandler.post(Runnable {
            if (requestMap[target]!=url || hasQuit){
                return@Runnable
            }
            requestMap.remove(target)
            onThumbnailDownloader(target,bitmap)
        })
        cache.put(url,bitmap)
    }
    /**
     * @param target ui Target of our Url
     * @param url our Url address
     */
    fun queueThumbnail(target:T, url:String){
        Log.i(TAG, "queueThumnail: got Url = $url")

        requestMap[target]=url
        /*
        when we have image in cache
         */
        cache.get(url)?.let {
            responseHandler.post(Runnable {
                if (hasQuit){
                    return@Runnable
                }
                requestMap.remove(target)
                onThumbnailDownloader(target,it)
            })
            return
        }
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD,target)
            .sendToTarget()

    }

}