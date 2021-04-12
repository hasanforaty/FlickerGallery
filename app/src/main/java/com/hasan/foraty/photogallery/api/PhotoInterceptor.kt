package com.hasan.foraty.photogallery.api

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
private const val API_KEY="79a7ec7cf5534f877d2d11d9a2ee2e30"
private const val TAG = "PhotoInterceptor"
class PhotoInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest:Request = chain.request()
        val newUrl:HttpUrl = originalRequest.url().newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("extras", "url_s")
                .addQueryParameter("safesearch", "1")
                .build()
        val newRequest:Request=originalRequest.newBuilder()
                .url(newUrl)
                .build()
        Log.d(TAG, "intercept: url : ${newRequest.url()}")
        return chain.proceed(newRequest)
    }
}