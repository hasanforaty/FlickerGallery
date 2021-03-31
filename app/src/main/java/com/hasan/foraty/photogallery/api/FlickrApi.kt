package com.hasan.foraty.photogallery.api

import retrofit2.Call
import retrofit2.http.GET

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
    @GET(
        "services/rest/?method=flickr.interestingness.getList" +
                "&api_key=$KEY" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s")
    fun fetchPhoto(): Call<FlickrResponse>
}
