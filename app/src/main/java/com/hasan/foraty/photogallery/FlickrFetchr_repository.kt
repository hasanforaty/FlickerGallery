package com.hasan.foraty.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hasan.foraty.photogallery.api.FlickrApi
import com.hasan.foraty.photogallery.api.FlickrResponse
import com.hasan.foraty.photogallery.api.PhotoResponse
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG="FlickrFetchr_repository"
class FlickrFetchr_repository {
    private val flickrApi:FlickrApi
    companion object{
        fun newInstance():FlickrFetchr_repository{
            return FlickrFetchr_repository()
        }
    }
    init {
        val spec:List<ConnectionSpec> = mutableListOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)
        val client=OkHttpClient.Builder().connectionSpecs(spec).build()
        val retrofit=Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        flickrApi=retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhoto():LiveData<List<GalleryItem>>{
        val result= MutableLiveData<List<GalleryItem>>()
        val flickrRequest=flickrApi.fetchPhoto()
        flickrRequest.enqueue(object : Callback<FlickrResponse>{
            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG,"response received")
                val flickrResponse:FlickrResponse?=response.body()
                val photoResponse:PhotoResponse?=flickrResponse?.photos
                var galleryItems:List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
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
}