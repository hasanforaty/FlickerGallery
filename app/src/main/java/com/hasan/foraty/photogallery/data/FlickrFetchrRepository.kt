package com.hasan.foraty.photogallery.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.hasan.foraty.photogallery.api.*
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG="FlickrFetchr_repository"
class FlickrFetchrRepository {
    private val flickrApi:FlickrApi
    companion object{
        fun newInstance(): FlickrFetchrRepository {
            return FlickrFetchrRepository()
        }
    }
    init {
        val spec:List<ConnectionSpec> = mutableListOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)
        val client=OkHttpClient.Builder()
                .addInterceptor(PhotoInterceptor())
                .connectionSpecs(spec)
                .build()

        //default method to Create DB
//        val retrofit=Retrofit.Builder()
//            .baseUrl("https://api.flickr.com/")
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
        //using custom Deserializer

        val customGson =GsonBuilder()
            .registerTypeAdapter(PhotoResponse::class.java,CustomFlickrDeserializer())
            .create()
        val retrofit=Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(customGson))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        flickrApi=retrofit.create(FlickrApi::class.java)
    }
    fun fetchPhoto():LiveData<List<GalleryItem>>{
        return fetchPhotoMetadata(flickrApi.fetchPhoto())
    }
    fun searchPhotos(query:String):LiveData<List<GalleryItem>>{
        return fetchPhotoMetadata(flickrApi.searchPhotos(query))
    }
    private fun fetchPhotoMetadata(flickrRequest:Call<FlickrResponse>):LiveData<List<GalleryItem>>{
        val result= MutableLiveData<List<GalleryItem>>()
        flickrRequest.enqueue(object : Callback<FlickrResponse>{
            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG,"response received")
                val flickrResponse:FlickrResponse?=response.body()
                val photoResponse:PhotoResponse?=flickrResponse?.photos
                val galleryItems:List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems.filterNot {
                    it.url.isEmpty()
                }
                result.value=galleryItems
            }
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }
        })
        return result
    }

    @WorkerThread
    fun fetchPhoto(url:String):Bitmap?{
        val response:Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use (BitmapFactory::decodeStream)
        Log.i(TAG, "fetchPhoto: Decode Bitmap=$bitmap from Response = $response")
        return bitmap
    }
}