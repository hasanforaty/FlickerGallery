package com.hasan.foraty.photogallery.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.hasan.foraty.photogallery.GalleryItem
import java.lang.reflect.Type

class CustomFlickrDeserializer:JsonDeserializer<PhotoResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {
        val photoResponse=PhotoResponse()
        val photos:MutableList<GalleryItem> = mutableListOf()
        json?.let {
           it.asJsonObject?.let {
               val listOfPhoto=it.get("photo").asJsonArray
               for (photo in listOfPhoto){
                   val photoGalleryObject=GalleryItem()
                   val jsonObjects=photo.asJsonObject
                   photoGalleryObject.id= jsonObjects.get("id").asString
                   photoGalleryObject.title=jsonObjects.get("title").asString
                   photoGalleryObject.url=jsonObjects.get("url_s").asString
                   photos.add(photoGalleryObject)
               }
           }
        }
        photoResponse.galleryItems=photos
        return photoResponse
    }
}