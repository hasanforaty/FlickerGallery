package com.hasan.foraty.photogallery.networking

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

private const val KEY="79a7ec7cf5534f877d2d11d9a2ee2e30"
/**
 * FlickrApi
 * interface for Flickr Site usable for retrofit
 */
interface FlickrApi {
    /**
     * fetchPhoto method to get a reference to Call object
     * @return Call object represent for get Annotation
     */
    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchPhoto(): Call<FlickrResponse>
    @GET
    fun fetchUrlBytes(@Url url:String):Call<ResponseBody>
    @GET("services/rest/?method=flickr.photos.search")
    fun searchPhotos(@Query("text")query:String):Call<FlickrResponse>
}
